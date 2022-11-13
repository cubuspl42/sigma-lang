package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.DictType
import sigma.types.PrimitiveType
import sigma.types.UnorderedTupleType
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable
import sigma.values.tables.Scope
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class UnorderedTupleLiteral(
    override val location: SourceLocation,
    val entries: List<EntryExpression>,
) : TupleLiteral() {
    class DuplicateKeyError(
        key: PrimitiveValue,
    ) : TypeError(
        message = "Duplicate key: ${key.dump()}",
    )

    class InconsistentKeysError : TypeError(
        message = "Inconsistent key types",
    )

    class InconsistentValuesError : TypeError(
        message = "Inconsistent image types",
    )

    class UnsupportedKeyError : TypeError(
        message = "Unsupported key type",
    )

    class UnsupportedValueError : TypeError(
        message = "Unsupported key type",
    )

    sealed interface EntryExpression {
        val key: Expression
        val value: Expression
    }

    data class NamedEntryExpression(
        val name: Symbol,
        override val value: Expression,
    ) : EntryExpression {
        override val key: SymbolLiteral = SymbolLiteral(
            location = SourceLocation.Invalid,
            symbol = name,
        )
    }

    data class ArbitraryEntryExpression(
        override val key: Expression,
        override val value: Expression,
    ) : EntryExpression

    data class EntryType(
        val keyType: PrimitiveType,
        val valueType: Type,
    )

    data class EntryLiteralType(
        val key: PrimitiveValue,
        val valueType: Type,
    )

    companion object {
        fun build(
            ctx: UnorderedTupleLiteralContext,
        ): UnorderedTupleLiteral = UnorderedTupleLiteral(
            location = SourceLocation.build(ctx),
            entries = ctx.association().map {
                buildAssignment(it)
            }
        )

        private fun buildAssignment(
            ctx: SigmaParser.AssociationContext,
        ): EntryExpression = object : SigmaParserBaseVisitor<EntryExpression>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = NamedEntryExpression(
                name = Symbol.of(ctx.name.text),
                value = Expression.build(ctx.image),
            )

            // TODO: Re-support dict literals
//            override fun visitArbitraryBindAlt(
//                ctx: SigmaParser.ArbitraryBindAltContext,
//            ) = ArbitraryEntryExpression(
//                key = Expression.build(ctx.key),
//                value = Expression.build(ctx.image),
//            )
        }.visit(ctx)
    }

    override fun dump(): String = "(dict constructor)"

    override fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val entryTypes = entries.map {
            val keyType = it.key.inferType(
                typeScope = typeScope,
                valueScope = valueScope,
            ) as? PrimitiveType ?: throw UnsupportedKeyError()

            val valueType = it.value.inferType(
                typeScope = typeScope,
                valueScope = valueScope,
            )

            EntryType(
                keyType = keyType,
                valueType = valueType,
            )
        }

        // Note: non-local return
        fun asEntryLiteralTypes(): List<EntryLiteralType>? = entryTypes.map {
            val keyLiteralType = it.keyType.asLiteral ?: return null

            EntryLiteralType(
                key = keyLiteralType.value,
                valueType = it.valueType,
            )
        }

        val entryLiteralTypes = asEntryLiteralTypes()

        if (entryLiteralTypes != null) {
            val valueTypeByKeyType = entryLiteralTypes.groupBy {
                it.key
            }.mapValues { (key, entryTypes) ->
                val valueTypes = entryTypes.map { it.valueType }

                valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key)
            }

            return UnorderedTupleType(
                valueTypeByKey = valueTypeByKeyType,
            )
        } else {
            val valueTypeByKeyType = entryTypes.groupBy {
                it.keyType
            }

            val (keyType, valueTypes) = valueTypeByKeyType.entries.singleOrNull() ?: throw InconsistentKeysError()

            val valueType = valueTypes.map { it.valueType }.toSet().singleOrNull() ?: throw InconsistentValuesError()

            return DictType(
                keyType = keyType,
                valueType = valueType,
            )
        }
    }

    override fun evaluate(
        scope: Scope,
    ): DictTable = DictTable(
        associations = entries.associate {
            val key = it.key.evaluate(scope = scope).toEvaluatedValue as PrimitiveValue
            val value = it.value.bind(scope = scope)

            key to value
        },
    )
}

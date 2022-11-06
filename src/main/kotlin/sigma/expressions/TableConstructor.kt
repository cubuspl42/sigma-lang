package sigma.expressions

import sigma.StaticScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.DictArrayAltContext
import sigma.parser.antlr.SigmaParser.DictContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.DictType
import sigma.types.LiteralType
import sigma.types.PrimitiveType
import sigma.types.StructType
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable
import sigma.values.tables.Scope
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class TableConstructor(
    override val location: SourceLocation,
    val entries: List<EntryExpression>,
) : Expression() {
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

    data class EntryType(
        val keyType: PrimitiveType,
        val valueType: Type,
    )

    data class EntryLiteralType(
        val keyType: LiteralType,
        val valueType: Type,
    )

    data class SymbolEntryExpression(
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

    companion object {
        fun build(
            ctx: DictContext,
        ): TableConstructor = object : SigmaParserBaseVisitor<TableConstructor>() {
            override fun visitDictTableAlt(
                ctx: SigmaParser.DictTableAltContext,
            ): TableConstructor = TableConstructor(
                location = SourceLocation.build(ctx),
                entries = buildFromTable(ctx.table()),
            )

            override fun visitDictArrayAlt(
                ctx: DictArrayAltContext,
            ): TableConstructor = TableConstructor(
                location = SourceLocation.build(ctx),
                entries = buildFromArray(ctx.content)
            )
        }.visit(ctx)

        private fun buildFromTable(
            ctx: SigmaParser.TableContext,
        ): List<EntryExpression> = ctx.tableBind().map {
            buildAssignment(it)
        }

        private fun buildFromArray(
            ctx: SigmaParser.ArrayContext,
        ): List<ArbitraryEntryExpression> = ctx.bindImage().withIndex().map { (index, imageCtx) ->
            // Note: When arbitrary binds aren't supported, then these int literals can be changed to values
            ArbitraryEntryExpression(
                key = IntLiteral.of(index),
                value = Expression.build(imageCtx.image),
            )
        }

        private fun buildAssignment(
            ctx: SigmaParser.TableBindContext,
        ): EntryExpression = object : SigmaParserBaseVisitor<EntryExpression>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = SymbolEntryExpression(
                name = Symbol.of(ctx.name.text),
                value = Expression.build(ctx.image.image),
            )

            override fun visitArbitraryBindAlt(
                ctx: SigmaParser.ArbitraryBindAltContext,
            ) = ArbitraryEntryExpression(
                key = Expression.build(ctx.key),
                value = Expression.build(ctx.image.image),
            )
        }.visit(ctx)
    }

    override fun dump(): String = "(dict constructor)"

    override fun inferType(
        scope: StaticScope,
    ): Type {
        val entryTypes = entries.map {
            EntryType(
                keyType = it.key.inferType(scope = scope) as? PrimitiveType ?: throw UnsupportedKeyError(),
                valueType = it.value.inferType(scope = scope),
            )
        }

        fun asEntryLiteralTypes(): List<EntryLiteralType>? = entryTypes.map {
            val keyType = it.keyType as? LiteralType ?: return null

            EntryLiteralType(
                keyType = keyType,
                valueType = it.valueType,
            )
        }

        val entryLiteralTypes = asEntryLiteralTypes()

        if (entryLiteralTypes != null) {
            val valueTypeByKeyType = entryLiteralTypes.groupBy {
                it.keyType
            }.mapValues { (key, entryTypes) ->
                val valueTypes = entryTypes.map { it.valueType }

                valueTypes.singleOrNull() ?: throw DuplicateKeyError(key = key.value)
            }

            return StructType(
                entries = valueTypeByKeyType,
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

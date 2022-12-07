package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.DictAssociationContext
import sigma.parser.antlr.SigmaParser.DictLiteralContext
import sigma.types.DictType
import sigma.types.PrimitiveType
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable
import sigma.values.tables.Scope
import sigma.types.Type
import sigma.values.TypeError

data class DictLiteral(
    override val location: SourceLocation,
    val associations: List<Association>,
) : TupleLiteral() {
    data class Association(
        val key: Expression,
        val value: Expression,
    ) {
        companion object {
            fun build(ctx: DictAssociationContext): Association {
                return Association(
                    key = Expression.build(ctx.key),
                    value = Expression.build(ctx.value),
                )
            }
        }
    }

    class InconsistentKeyTypesError(
        inconsistentKeyTypes: Set<Type>,
        location: SourceLocation,
    ) : TypeError(
        location = location,
        message = "Dict literal keys have different types: ${inconsistentKeyTypes}",
    )

    class InconsistentValueTypesError(
        location: SourceLocation,
    ) : TypeError(
        location = location,
        message = "Dict literal values have different types",
    )

    class NonPrimitiveKeyTypeError(
        location: SourceLocation,
    ) : TypeError(
        location = location,
        message = "Dict literal key type is not primitive",
    )

    companion object {
        fun build(
            ctx: DictLiteralContext,
        ): DictLiteral = DictLiteral(
            location = SourceLocation.build(ctx),
            associations = ctx.dictAssociation().map {
                Association.build(it)
            },
        )
    }

    override fun dump(): String = "(dict literal)"

    override fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val keyTypes = associations.map {
            it.key.inferType(typeScope = typeScope, valueScope = valueScope)
        }.toSet()

        val keyType = (keyTypes.singleOrNull() ?: throw InconsistentKeyTypesError(
            inconsistentKeyTypes = keyTypes,
            location = location,
        )) as? PrimitiveType ?: throw NonPrimitiveKeyTypeError(
            location = location,
        )

        val valueTypes = associations.map {
            it.value.inferType(typeScope = typeScope, valueScope = valueScope)
        }.toSet()

        val valueType = valueTypes.singleOrNull() ?: throw InconsistentValueTypesError(
            location = location,
        )

        return DictType(
            keyType = keyType,
            valueType = valueType,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): DictTable = DictTable(
        entries = associations.associate {
            val key = it.key.evaluate(scope = scope).toEvaluatedValue as PrimitiveValue
            val value = it.value.bind(scope = scope)

            key to value
        },
    )
}

package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.DictAssociationContext
import sigma.parser.antlr.SigmaParser.DictLiteralContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.DictType
import sigma.semantics.types.PrimitiveType
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.values.tables.Scope
import sigma.semantics.types.Type
import sigma.evaluation.values.TypeErrorException

data class DictLiteralTerm(
    override val location: SourceLocation,
    val associations: List<Association>,
) : ExpressionTerm() {
    data class Association(
        val key: ExpressionTerm,
        val value: ExpressionTerm,
    ) {
        companion object {
            fun build(ctx: DictAssociationContext): Association {
                return Association(
                    key = ExpressionTerm.build(ctx.key),
                    value = ExpressionTerm.build(ctx.value),
                )
            }
        }
    }

    class InconsistentKeyTypesError(
        inconsistentKeyTypes: Set<Type>,
        location: SourceLocation,
    ) : TypeErrorException(
        location = location,
        message = "Dict literal keys have different types: ${inconsistentKeyTypes}",
    )

    class InconsistentValueTypesError(
        location: SourceLocation,
    ) : TypeErrorException(
        location = location,
        message = "Dict literal values have different types",
    )

    class NonPrimitiveKeyTypeError(
        location: SourceLocation,
    ) : TypeErrorException(
        location = location,
        message = "Dict literal key type is not primitive",
    )

    companion object {
        fun build(
            ctx: DictLiteralContext,
        ): DictLiteralTerm = DictLiteralTerm(
            location = SourceLocation.build(ctx),
            associations = ctx.dictAssociation().map {
                Association.build(it)
            },
        )
    }

    override fun dump(): String = "(dict literal)"

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type {
        val keyTypes = associations.map {
            it.key.determineType(typeScope = typeScope, valueScope = valueScope)
        }.toSet()

        val keyType = (keyTypes.singleOrNull() ?: throw InconsistentKeyTypesError(
            inconsistentKeyTypes = keyTypes,
            location = location,
        )) as? PrimitiveType ?: throw NonPrimitiveKeyTypeError(
            location = location,
        )

        val valueTypes = associations.map {
            it.value.determineType(typeScope = typeScope, valueScope = valueScope)
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

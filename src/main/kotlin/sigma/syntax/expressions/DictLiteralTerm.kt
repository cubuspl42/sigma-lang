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
import sigma.evaluation.scope.Scope
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
    override fun determineType(typeScope: TypeScope, valueScope: SyntaxValueScope): Type {
        TODO("Not yet implemented")
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

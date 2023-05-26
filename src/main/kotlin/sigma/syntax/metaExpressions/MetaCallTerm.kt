package sigma.syntax.metaExpressions

import sigma.TypeScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.Type
import sigma.parser.antlr.SigmaParser

data class MetaCallTerm(
    override val location: SourceLocation,
    val callee: MetaReferenceTerm,
    val passedArgument: TypeTupleLiteral,
) : MetaExpressionTerm() {
    data class TypeTupleLiteral(
        val elements: List<MetaExpressionTerm>,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.TypeTupleLiteralContext,
            ): TypeTupleLiteral = TypeTupleLiteral(
                elements = ctx.elements.map { MetaExpressionTerm.build(it) },
            )
        }

    }

    companion object {
        fun build(
            ctx: SigmaParser.TypeCallContext,
        ): MetaCallTerm = MetaCallTerm(
            location = SourceLocation.build(ctx),
            callee = MetaReferenceTerm.build(ctx.callee),
            passedArgument = TypeTupleLiteral.build(ctx.passedArgument),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): Type = TODO()
}

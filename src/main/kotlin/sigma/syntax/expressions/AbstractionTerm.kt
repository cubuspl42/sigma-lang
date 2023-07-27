package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.syntax.SourceLocation

data class AbstractionTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
    val declaredImageType: ExpressionTerm? = null,
    val image: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: AbstractionContext,
        ): AbstractionTerm = AbstractionTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                ExpressionTerm.build(it)
            },
            image = ExpressionTerm.build(ctx.image),
        )
    }

    override fun dump(): String = "(abstraction)"
}

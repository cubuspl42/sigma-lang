package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.syntax.SourceLocation

data class AbstractionSourceTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorSourceTerm,
    val declaredImageType: ExpressionSourceTerm? = null,
    val image: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: AbstractionContext,
        ): AbstractionSourceTerm = AbstractionSourceTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorSourceTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                ExpressionSourceTerm.build(it)
            },
            image = ExpressionSourceTerm.build(ctx.image),
        )
    }

    override fun dump(): String = "(abstraction)"
}

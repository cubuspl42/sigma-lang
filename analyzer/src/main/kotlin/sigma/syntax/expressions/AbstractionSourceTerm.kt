package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.syntax.SourceLocation

data class AbstractionSourceTerm(
    override val location: SourceLocation,
    override val genericParametersTuple: GenericParametersTuple? = null,
    override val argumentType: TupleTypeConstructorSourceTerm,
    override val declaredImageType: ExpressionSourceTerm? = null,
    override val image: ExpressionSourceTerm,
) : ExpressionSourceTerm(), AbstractionTerm {
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

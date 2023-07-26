package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeConstructorTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class AbstractionTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
    val declaredImageType: TypeExpressionTerm? = null,
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
                TypeExpressionTerm.build(it)
            },
            image = ExpressionTerm.build(ctx.image),
        )
    }

    override fun dump(): String = "(abstraction)"
}

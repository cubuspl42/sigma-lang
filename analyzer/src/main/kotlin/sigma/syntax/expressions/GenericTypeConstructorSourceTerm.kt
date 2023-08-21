package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.syntax.SourceLocation

data class GenericTypeConstructorSourceTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple,
    val body: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): GenericTypeConstructor = GenericTypeConstructor(
//        context = declarationScope,
//        argumentMetaType = genericParametersTuple,
//        bodyTerm = body,
//        body = body.evaluate(
//            declarationScope = genericParametersTuple.asDeclarationBlock.chainWith(
//                outerScope = declarationScope,
//            ),
//        ),
//    )

    companion object {
        fun build(
            ctx: SigmaParser.GenericTypeConstructorContext,
        ): ExpressionSourceTerm = GenericTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = GenericParametersTuple.build(ctx.genericParametersTuple()),
            body = build(ctx.body),
        )
    }

    override fun dump(): String = "(generic type constructor)"
}

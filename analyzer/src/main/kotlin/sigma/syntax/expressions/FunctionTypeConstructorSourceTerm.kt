package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.FunctionTypeConstructorContext
import sigma.syntax.SourceLocation

// Thought: "FunctionTypeConstructorTerm"?
data class FunctionTypeConstructorSourceTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple?,
    val argumentType: TupleTypeConstructorSourceTerm,
    val imageType: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: FunctionTypeConstructorContext,
        ): FunctionTypeConstructorSourceTerm = FunctionTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorSourceTerm.build(it)
            },
            imageType = ExpressionSourceTerm.build(ctx.imageType),
        )
    }

    override fun dump(): String = "(function type constructor)"

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): TypeEntity {
//        val innerDeclarationScope = genericParametersTuple?.asDeclarationBlock?.chainWith(
//            outerScope = declarationScope,
//        ) ?: declarationScope
//
//        val argumentType = argumentType.evaluate(
//            declarationScope = innerDeclarationScope,
//        )
//
//        val imageType = this.imageType.evaluateAsType(
//            declarationScope = innerDeclarationScope,
//        )
//
//        return UniversalFunctionType(
//            argumentType = argumentType,
//            imageType = imageType,
//        )
//    }
}

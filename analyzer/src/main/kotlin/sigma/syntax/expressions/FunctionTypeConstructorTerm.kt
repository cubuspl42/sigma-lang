package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.syntax.SourceLocation

// Thought: "FunctionTypeConstructorTerm"?
data class FunctionTypeConstructorTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple?,
    val argumentType: TupleTypeConstructorTerm,
    val imageType: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: FunctionTypeDepictionContext,
        ): FunctionTypeConstructorTerm = FunctionTypeConstructorTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorTerm.build(it)
            },
            imageType = ExpressionTerm.build(ctx.imageType),
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

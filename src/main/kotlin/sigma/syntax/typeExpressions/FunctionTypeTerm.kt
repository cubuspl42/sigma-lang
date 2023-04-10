package sigma.syntax.typeExpressions

import sigma.SyntaxTypeScope
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm
import sigma.semantics.types.UniversalFunctionType

data class FunctionTypeTerm(
    override val location: SourceLocation,
    val genericParametersTuple: AbstractionTerm.GenericParametersTuple? = null,
    val argumentType: TupleTypeLiteralTerm,
    val imageType: TypeExpressionTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: FunctionTypeDepictionContext,
        ): FunctionTypeTerm = FunctionTypeTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                AbstractionTerm.GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeLiteralTerm.build(it)
            },
            imageType = TypeExpressionTerm.build(ctx.imageType),
        )
    }

    override fun evaluate(
        typeScope: SyntaxTypeScope,
    ): UniversalFunctionType {
        val innerTypeScope = genericParametersTuple?.toStaticTypeScope()?.chainWith(
            backScope = typeScope,
        ) ?: typeScope

        val argumentType = argumentType.evaluate(
            typeScope = innerTypeScope,
        )

        val imageType = this.imageType.evaluate(
            typeScope = innerTypeScope,
        )

        return UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )
    }
}

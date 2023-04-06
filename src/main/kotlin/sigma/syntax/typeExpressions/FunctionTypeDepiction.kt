package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.Abstraction
import sigma.semantics.types.UniversalFunctionType

data class FunctionTypeDepiction(
    override val location: SourceLocation,
    val genericParametersTuple: Abstraction.GenericParametersTuple? = null,
    val argumentType: TupleTypeLiteral,
    val imageType: TypeExpression,
) : TypeExpression() {
    companion object {
        fun build(
            ctx: FunctionTypeDepictionContext,
        ): FunctionTypeDepiction = FunctionTypeDepiction(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                Abstraction.GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeLiteral.build(it)
            },
            imageType = TypeExpression.build(ctx.imageType),
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
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

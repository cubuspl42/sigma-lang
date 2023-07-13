package sigma.syntax.typeExpressions

import sigma.semantics.TypeScope
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.semantics.types.TypeEntity
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm
import sigma.semantics.types.UniversalFunctionType

data class FunctionTypeTerm(
    override val location: SourceLocation,
    val genericParametersTuple: AbstractionTerm.GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
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
                TupleTypeConstructorTerm.build(it)
            },
            imageType = TypeExpressionTerm.build(ctx.imageType),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): TypeEntity {
        val innerTypeScope = genericParametersTuple?.toStaticTypeScope(
            typeScope = typeScope,
        ) ?: typeScope

        val argumentType = argumentType.evaluate(
            typeScope = innerTypeScope,
        )

        val imageType = this.imageType.evaluateAsType(
            typeScope = innerTypeScope,
        )

        return UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )
    }
}

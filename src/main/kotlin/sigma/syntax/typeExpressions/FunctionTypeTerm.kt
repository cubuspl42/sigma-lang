package sigma.syntax.typeExpressions

import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.semantics.StaticScope
import sigma.semantics.types.TypeEntity
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.GenericParametersTuple

data class FunctionTypeTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
    val imageType: TypeExpressionTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: FunctionTypeDepictionContext,
        ): FunctionTypeTerm = FunctionTypeTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorTerm.build(it)
            },
            imageType = TypeExpressionTerm.build(ctx.imageType),
        )
    }

    override fun evaluate(
        declarationScope: StaticScope,
    ): TypeEntity {
        val innerDeclarationScope = genericParametersTuple?.asDeclarationBlock?.chainWith(
            outerScope = declarationScope,
        ) ?: declarationScope

        val argumentType = argumentType.evaluate(
            declarationScope = innerDeclarationScope,
        )

        val imageType = this.imageType.evaluateAsType(
            declarationScope = innerDeclarationScope,
        )

        return UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )
    }
}

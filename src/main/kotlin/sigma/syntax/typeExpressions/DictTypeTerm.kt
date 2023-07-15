package sigma.syntax.typeExpressions

import sigma.semantics.DeclarationScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.DictType
import sigma.semantics.types.PrimitiveType
import sigma.evaluation.values.TypeErrorException
import sigma.semantics.types.TypeEntity

data class DictTypeTerm(
    override val location: SourceLocation,
    val keyType: TypeExpressionTerm,
    val valueType: TypeExpressionTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.DictTypeDepictionContext,
        ): DictTypeTerm = DictTypeTerm(
            location = SourceLocation.build(ctx),
            keyType = build(ctx.keyType),
            valueType = build(ctx.valueType),
        )
    }

    override fun evaluate(
        declarationScope: DeclarationScope,
    ): TypeEntity = DictType(
        keyType = keyType.evaluate(
            declarationScope = declarationScope,
        ) as? PrimitiveType ?: throw TypeErrorException(
            location = keyType.location,
            message = "Dict key type is not primitive",
        ),
        valueType = valueType.evaluateAsType(
            declarationScope = declarationScope,
        ),
    )
}

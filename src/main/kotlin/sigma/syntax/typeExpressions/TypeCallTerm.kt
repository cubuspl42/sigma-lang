package sigma.syntax.typeExpressions

import sigma.TypeScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.Type
import sigma.evaluation.values.TypeErrorException
import sigma.parser.antlr.SigmaParser

data class TypeCallTerm(
    override val location: SourceLocation,
    val callee: TypeReferenceTerm,
    val passedArgument: TypeTupleConstructor,
) : TypeExpressionTerm() {
    data class TypeTupleConstructor(
        val elements: List<TypeExpressionTerm>,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.TypeTupleConstructorContext,
            ): TypeTupleConstructor = TypeTupleConstructor(
                elements = ctx.elements.map { TypeExpressionTerm.build(it) },
            )
        }

    }

    companion object {
        fun build(
            ctx: SigmaParser.TypeCallContext,
        ): TypeCallTerm = TypeCallTerm(
            location = SourceLocation.build(ctx),
            callee = TypeReferenceTerm.build(ctx.callee),
            passedArgument = TypeTupleConstructor.build(ctx.passedArgument),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): Type = TODO()
}

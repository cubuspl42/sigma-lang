package sigma.syntax.typeExpressions

import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.TypeErrorException
import sigma.semantics.DeclarationScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.GenericTypeConstructor
import sigma.semantics.types.OrderedTypeTuple
import sigma.semantics.types.TypeEntity

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

        fun evaluate(
            declarationScope: DeclarationScope,
        ): OrderedTypeTuple = OrderedTypeTuple(
            elements = elements.map {
                it.evaluate(declarationScope = declarationScope)
            },
        )

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
        declarationScope: DeclarationScope,
    ): TypeEntity {
        val calleeEntity = callee.evaluate(declarationScope = declarationScope)

        if (calleeEntity !is GenericTypeConstructor) {
            throw TypeErrorException(
                location = callee.location,
                message = "Callee $calleeEntity is not a type constructor",
            )
        }

        val argumentEntity = passedArgument.evaluate(declarationScope = declarationScope)

        val result = calleeEntity.call(argumentEntity)

        return result
    }
}

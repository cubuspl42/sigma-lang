package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.ArrayTypeConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm

class ArrayTypeConstructor(
    override val term: ExpressionTerm,
    val elementType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: ArrayTypeConstructorTerm,
        ): ArrayTypeConstructor = ArrayTypeConstructor(
            term = term,
            elementType = Expression.build(
                declarationScope = declarationScope,
                term = term.elementType,
            ),
        )
    }

    override val inferredType: Computation<Type> = TODO()

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }

    override fun evaluate(
        context: EvaluationContext,
        scope: Scope,
    ): Value = TODO()
}

package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm

class UnorderedTupleTypeConstructor(
    override val term: UnorderedTupleTypeConstructorTerm,
    val entries: Set<UnorderedTupleConstructor.Entry>,
) : TupleTypeConstructor() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: UnorderedTupleTypeConstructorTerm,
        ): UnorderedTupleTypeConstructor = UnorderedTupleTypeConstructor(
            term = term,
            entries = term.entries.map {
                UnorderedTupleConstructor.Entry.build(
                    declarationScope = declarationScope,
                    entry = it,
                )
            }.toSet(),
        )
    }


    override val inferredType: Computation<Type> = TODO()

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }

    override fun evaluateDirectly(
        context: EvaluationContext,
        scope: Scope,
    ): EvaluationResult = TODO()
}

package sigma.semantics.expressions


import sigma.evaluation.scope.Scope
import sigma.evaluation.values.EvaluationResult
import sigma.semantics.Computation
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.Type
import sigma.syntax.expressions.IntLiteralTerm

data class IntLiteral(
    override val term: IntLiteralTerm,
) : Expression() {
    companion object {
        fun build(
            term: IntLiteralTerm,
        ): IntLiteral = IntLiteral(
            term = term,
        )
    }

    val value: IntValue
        get() = term.value

    override val inferredType: Computation<Type> = Computation.pure(
        IntLiteralType(
            value = value,
        )
    )

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(
        scope: Scope,
    ): Thunk<*> = value.asThunk
}

package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.UndefinedValue
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateInitialValue
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.syntax.expressions.IsUndefinedCheckTerm

data class IsUndefinedCheck(
    override val term: IsUndefinedCheckTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: IsUndefinedCheckTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            term = term,
            argument = Expression.build(
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    override val inferredType: Thunk<Type> = Thunk.pure(BoolType)
    override fun bind(scope: Scope): Thunk<Value> {
        val argumentValue = argument.bind(
            scope = scope,
        ).evaluateInitialValue()

        return BoolValue(
            value = argumentValue is UndefinedValue,
        ).asThunk
    }

    override val errors: Set<SemanticError> = emptySet()
}

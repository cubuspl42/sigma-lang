package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.UndefinedValue
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.syntax.expressions.IsUndefinedCheckSourceTerm

data class IsUndefinedCheck(
    override val term: IsUndefinedCheckSourceTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: IsUndefinedCheckSourceTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            term = term,
            argument = Expression.build(
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    override val inferredType: Thunk<Type> = Thunk.pure(BoolType)
    override fun bind(scope: Scope): Thunk<Value> = argument.bind(
        scope = scope,
    ).thenJust { argumentValue ->
        BoolValue(
            value = argumentValue is UndefinedValue,
        )
    }

    override val errors: Set<SemanticError> = emptySet()
}

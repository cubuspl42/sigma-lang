package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.UndefinedValue
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.syntax.expressions.IsUndefinedCheckTerm

data class IsUndefinedCheck(
    override val term: IsUndefinedCheckTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: DeclarationScope,
            term: IsUndefinedCheckTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            term = term,
            argument = Expression.build(
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    override val inferredType: Computation<Type> = Computation.pure(BoolType)

    override val errors: Set<SemanticError> = emptySet()

    override fun evaluate(
        scope: Scope,
    ): Value {
        val argumentValue = argument.evaluate(scope = scope)

        return BoolValue(
            value = argumentValue is UndefinedValue,
        )
    }
}

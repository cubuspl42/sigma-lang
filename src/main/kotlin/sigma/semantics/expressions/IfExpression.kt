package sigma.semantics.expressions

import sigma.evaluation.Thunk
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.TypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.IfExpressionTerm

class IfExpression(
    override val term: IfExpressionTerm,
    val guard: Expression,
    val trueBranch: Expression,
    val falseBranch: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: IfExpressionTerm,
        ): IfExpression = IfExpression(
            term = term,
            guard = build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.guard,
            ),
            trueBranch = build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.trueBranch,
            ),
            falseBranch = build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.falseBranch,
            ),
        )
    }

    sealed interface GuardValidationOutcome

    object ValidGuardResult : GuardValidationOutcome

    data class InvalidGuardError(
        override val location: SourceLocation,
        val actualType: Type,
    ) : GuardValidationOutcome, SemanticError {
        override fun dump(): String = "$location: Invalid guard type: ${actualType.dump()} (should be: Bool)"
    }

    private val guardValidationOutcome: Computation<GuardValidationOutcome?> =
        this.guard.inferredType.thenJust { guardType ->
            when (guardType) {
                is BoolType -> ValidGuardResult
                else -> InvalidGuardError(
                    location = guard.location,
                    actualType = guardType,
                )
            }
        }

    override val inferredType: Computation<Type> = Computation.combine2(
        trueBranch.inferredType,
        falseBranch.inferredType,
    ) {
            trueType,
            falseType,
        ->
        trueType.findLowestCommonSupertype(falseType)
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            guardValidationOutcome.value as? InvalidGuardError,
        ) + trueBranch.errors + falseBranch.errors
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk = object : Thunk() {
        override val toEvaluatedValue: Value
            get() {
                val guardValue = guard.evaluate(scope = scope).toEvaluatedValue

                if (guardValue !is BoolValue) throw IllegalArgumentException("Guard value $guardValue is not a boolean")

                return if (guardValue.value) {
                    trueBranch.evaluate(scope = scope).toEvaluatedValue
                } else {
                    falseBranch.evaluate(scope = scope).toEvaluatedValue
                }
            }

        override fun dump(): String = "(bound if)"
    }
}

package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
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
            declarationScope: StaticScope,
            term: IfExpressionTerm,
        ): IfExpression = IfExpression(
            term = term,
            guard = build(
                declarationScope = declarationScope,
                term = term.guard,
            ),
            trueBranch = build(
                declarationScope = declarationScope,
                term = term.trueBranch,
            ),
            falseBranch = build(
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
        context: EvaluationContext,
        scope: Scope,
    ): EvaluationResult {

        val guardValue = guard.evaluate(
            context = context,
            scope = scope,
        )

        if (guardValue !is BoolValue) throw IllegalArgumentException("Guard value $guardValue is not a boolean")

        return if (guardValue.value) {
            trueBranch.evaluate(
                context = context,
                scope = scope,
            )
        } else {
            falseBranch.evaluate(
                context = context,
                scope = scope,
            )
        }
    }
}

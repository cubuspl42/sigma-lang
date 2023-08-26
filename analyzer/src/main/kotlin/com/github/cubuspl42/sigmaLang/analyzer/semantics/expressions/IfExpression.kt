package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionTerm

class IfExpression(
    override val outerScope: StaticScope,
    override val term: IfExpressionTerm,
    val guard: Expression,
    val trueBranch: Expression,
    val falseBranch: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: IfExpressionTerm,
        ): IfExpression = IfExpression(
            outerScope = outerScope,
            term = term,
            guard = build(
                outerScope = outerScope,
                term = term.guard,
            ),
            trueBranch = build(
                outerScope = outerScope,
                term = term.trueBranch,
            ),
            falseBranch = build(
                outerScope = outerScope,
                term = term.falseBranch,
            ),
        )
    }

    sealed interface GuardValidationOutcome

    data object ValidGuardResult : GuardValidationOutcome

    data class InvalidGuardError(
        override val location: SourceLocation?,
        val actualType: Type,
    ) : GuardValidationOutcome, SemanticError {
        override fun dump(): String = "$location: Invalid guard type: ${actualType.dump()} (should be: Bool)"
    }

    private val guardValidationOutcome: Thunk<GuardValidationOutcome?> =
        this.guard.inferredType.thenJust { guardType ->
            when (guardType) {
                is BoolType -> ValidGuardResult
                else -> InvalidGuardError(
                    location = guard.location,
                    actualType = guardType,
                )
            }
        }

    override val inferredType: Thunk<Type> = Thunk.combine2(
        trueBranch.inferredType,
        falseBranch.inferredType,
    ) {
            trueType,
            falseType,
        ->
        trueType.findLowestCommonSupertype(falseType)
    }

    override fun bind(scope: Scope): Thunk<Value> = guard.bind(
        scope = scope,
    ).thenDo { guardValue ->
        if (guardValue !is BoolValue) throw IllegalArgumentException("Guard value $guardValue is not a boolean")

        if (guardValue.value) {
            trueBranch.bind(
                scope = scope,
            )
        } else {
            falseBranch.bind(
                scope = scope,
            )
        }
    }

    override val subExpressions: Set<Expression> = setOf(guard, trueBranch, falseBranch)

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            guardValidationOutcome.value as? InvalidGuardError,
        ) + guard.errors + trueBranch.errors + falseBranch.errors
    }
}

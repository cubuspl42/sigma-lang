package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckSourceTerm

data class IsUndefinedCheck(
    override val outerScope: StaticScope,
    override val term: IsUndefinedCheckSourceTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: IsUndefinedCheckSourceTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            outerScope = outerScope,
            term = term,
            argument = Expression.build(
                outerScope = outerScope,
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

    override val subExpressions: Set<Expression> = setOf(argument)

    override val errors: Set<SemanticError> = emptySet()
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions


import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm

data class IntLiteral(
    override val term: IntLiteralSourceTerm,
    override val outerScope: StaticScope,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: IntLiteralSourceTerm,
        ): IntLiteral = IntLiteral(
            outerScope = outerScope,
            term = term,
        )
    }

    val value: IntValue
        get() = term.value

    override val inferredType: Thunk<Type> = Thunk.pure(
        IntLiteralType(
            value = value,
        )
    )

    override val subExpressions: Set<Expression> = emptySet()

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = value.asThunk
}

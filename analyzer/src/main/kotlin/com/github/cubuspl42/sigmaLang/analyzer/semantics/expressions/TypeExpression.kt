package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object TypeExpression {
    fun build(
        outerScope: StaticScope,
        term: ExpressionTerm,
    ): Stub<Expression> = Expression.build(
        context = Expression.BuildContext(
            outerScope = outerScope, // TODO: Shift
        ),
        term = term,
    )
}

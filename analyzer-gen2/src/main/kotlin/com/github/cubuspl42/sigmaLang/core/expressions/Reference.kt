package com.github.cubuspl42.sigmaLang.core.expressions

sealed class Reference : Expression() {
    final override val subExpressions: Set<Expression> = emptySet()
}

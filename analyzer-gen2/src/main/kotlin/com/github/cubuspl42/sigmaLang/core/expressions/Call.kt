package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value

class Call(
    val calleeLazy: Lazy<Expression>,
    val passedArgumentLazy: Lazy<Expression>,
) : Expression() {
    private val callee by calleeLazy
    private val passedArgument by passedArgumentLazy

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValue = callee.bind(scope = scope).value as Abstraction
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext

class Call(
    val calleeLazy: Lazy<Expression>,
    val passedArgumentLazy: Lazy<Expression>,
) : Expression() {
    companion object {
        fun fieldRead(
            subjectLazy: Lazy<Expression>,
            readFieldName: Identifier,
        ): Lazy<Expression> = lazyOf(
            Call(
                calleeLazy = subjectLazy,
                passedArgumentLazy = lazyOf(Literal(value = readFieldName)),
            ),
        )
    }

    private val callee by calleeLazy
    private val passedArgument by passedArgumentLazy

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValue = callee.bind(scope = scope).value as Callable
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

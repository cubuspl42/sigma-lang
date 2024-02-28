package com.github.cubuspl42.sigmaLang.core.expressions

class Call(
    val calleeLazy: Lazy<Expression>,
    val passedArgumentLazy: Lazy<Expression>,
): Expression() {
    val callee by calleeLazy
    val passedArgument by passedArgumentLazy
}

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import org.antlr.v4.runtime.Token

enum class InfixOperator(
    val symbol: String,
) {
    Multiply("*"), Divide("/"), Add("+"), Subtract("-"), LessThan("<"), LessThanEqual("<="), GreaterThan(">"), GreaterThanEqual(
        ">="
    ),
    Equals("=="), Link("..");

    companion object {
        fun build(
            ctx: Token,
        ): InfixOperator = fromSymbol(symbol = ctx.text)

        fun fromSymbol(
            symbol: String,
        ): InfixOperator = values().single { it.symbol == symbol }
    }
}

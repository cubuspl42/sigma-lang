package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.terms.ExpressionTerm
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

fun main() {
    val source = getResourceAsText("main.sigma")
    val sourceName = "__main__"

    val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
    val tokenStream = CommonTokenStream(lexer)
    val parser = SigmaParser(tokenStream)

    val program = parser.expression()

    val rootExpressionTerm = ExpressionTerm.build(program)

    val rootExpression = rootExpressionTerm.construct(
        context = ConstructionContext.Empty,
    ).value

    val rootValue = rootExpression.bind(
        scope = DynamicScope.Empty,
    ).value

    println(rootValue)
}

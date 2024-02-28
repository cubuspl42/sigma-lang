package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main() {
    val source = "{{}: {{}: {}}}"
    val sourceName = "__main__"

    val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
    val tokenStream = CommonTokenStream(lexer)
    val parser = SigmaParser(tokenStream)

    val program = parser.program()

    val root = Expression.build(program.expression())

    println(root.dump())
}

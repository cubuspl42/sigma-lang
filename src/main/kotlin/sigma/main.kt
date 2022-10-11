package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser

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

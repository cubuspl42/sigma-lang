import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.Expression
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser

const val sourceName = "problem6.sigma"

fun main() {
    val source = getResourceAsText(sourceName) ?: throw RuntimeException("Couldn't load the source file")

    val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
    val tokenStream = CommonTokenStream(lexer)
    val parser = SigmaParser(tokenStream)

    val program = parser.program()

    val root = Expression.build(program.expression())

    val result = root.evaluate()

    println(result)
}

private fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

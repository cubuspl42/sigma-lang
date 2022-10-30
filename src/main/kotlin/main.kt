import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.expressions.Expression
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

const val sourceName = "problem.sigma"

fun main() {
    val runtimeMxBean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
    val arguments: List<String> = runtimeMxBean.getInputArguments()

    println("Arguments: $arguments")

    val source = getResourceAsText(sourceName) ?: throw RuntimeException("Couldn't load the source file")

    val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
    val tokenStream = CommonTokenStream(lexer)
    val parser = SigmaParser(tokenStream)

    val program = parser.program()

    val root = Expression.build(program.expression())

    val result = root.evaluateAsRoot()

    println(result)
}

private fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

package sigma.programs.euler

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.BoolValue
import sigma.Expression
import sigma.IntValue
import sigma.Value
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import kotlin.test.Test
import kotlin.test.assertEquals

class EulerProblemsTests {
    @Test
    fun testProblem6() {
        assertEquals(
            expected = IntValue(25164150),
            actual = solveProblem(6),
        )
    }

    @Test
    fun testProblem7() {
        assertEquals(
            expected = BoolValue.False,
            actual = solveProblem(7),
        )
    }

    @Test
    fun testProblem8() {
        assertEquals(
            expected = BoolValue.False,
            actual = solveProblem(8),
        )
    }

    @Test
    fun testProblem9() {
        assertEquals(
            expected = BoolValue.False,
            actual = solveProblem(9),
        )
    }

    @Test
    fun testProblem10() {
        assertEquals(
            expected = BoolValue.False,
            actual = solveProblem(10),
        )
    }
}

private fun solveProblem(n: Int): Value {
    val sourceName = "problem$n.sigma"

    val source = getResourceAsText(sourceName) ?: throw RuntimeException("Couldn't load the source file `$sourceName`")

    val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
    val tokenStream = CommonTokenStream(lexer)
    val parser = SigmaParser(tokenStream)

    val program = parser.program()

    val root = Expression.build(program.expression())

    val result = root.evaluate()

    return result
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

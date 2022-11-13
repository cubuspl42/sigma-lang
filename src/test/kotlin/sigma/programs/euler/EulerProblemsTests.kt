package sigma.programs.euler

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.BuiltinScope
import sigma.GlobalTypeScope
import sigma.values.BoolValue
import sigma.expressions.Expression
import sigma.values.IntValue
import sigma.values.Value
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
    // TODO: Re-support dict literals
    fun testProblem7() {
        // For 20th prime (for performance reasons)
        assertEquals(
            expected = IntValue(71),
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

    root.validate(
        typeScope = GlobalTypeScope,
        valueScope = BuiltinScope,
    )

    val result = root.evaluateAsRoot()

    return result.toEvaluatedValue
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

package sigma.programs.euler

import sigma.compiler.Compiler
import sigma.values.BoolValue
import sigma.values.IntValue
import sigma.values.Value
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
        val actual = solveProblem(8)

        assertEquals(
            expected = IntValue(value = 23514624000L),
            actual = actual,
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

    val program = Compiler.initialize().load(
        sourceName = sourceName,
        source = source,
    )

    // TODO: Validate program
    // program.inferResultType()

    return program.evaluateResult()
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

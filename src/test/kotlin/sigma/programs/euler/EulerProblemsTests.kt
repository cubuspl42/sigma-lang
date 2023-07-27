package sigma.programs.euler

import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.CallStackExhaustionError
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.EvaluationResult
import sigma.semantics.Project
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
        // For 20th prime (for performance reasons)
        assertEquals(
            expected = IntValue(71),
            actual = solveProblem(7),
        )
    }

    @Test
    fun testProblem8() {
        val result = solveProblem(8)

        assertEquals(
            expected = IntValue(value = 23514624000L),
            actual = result,
        )
    }

    @Test
    fun testProblem9() {
        val result = solveProblem(9)

        assertIs<DictValue>(result)

        assertEquals<Map<PrimitiveValue, Value>>(
            expected = mapOf(
                Symbol.of("a") to IntValue(value = 1L),
                Symbol.of("b") to IntValue(value = 1L),
                Symbol.of("c") to IntValue(value = 8L),
            ),
            actual = result.entries,
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

private fun solveProblem(n: Int): EvaluationResult {
    val store = Project.ResourceStore(javaClass = EulerProblemsTests::class.java)
    val loader = Project.Loader.create(store = store)
    val program = loader.load(fileBaseName = "problem$n")

    val errors = program.errors

    println()
    println("[Problem $n]")
    println()

    if (errors.isNotEmpty()) {
        println("Semantic errors:")
        errors.forEach {
            println(it.dump())
        }
    }

    val result = program.evaluateResult()

    println()

    when (val evaluationResult = program.evaluateResult()) {
        CallStackExhaustionError -> println("Error: call stack exhausted")
        is Value -> println("Result: ${evaluationResult.dump()}")
    }

    println()

    return result
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

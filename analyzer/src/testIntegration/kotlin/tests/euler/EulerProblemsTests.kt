package tests.euler

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EulerProblemsTests {
    @Test
    fun testProblem6() {
        assertEquals(
            expected = EvaluationResult(IntValue(25164150)),
            actual = solveProblem(6),
        )
    }

    @Test
    fun testProblem7() {
        // For 20th prime (for performance reasons)
        assertEquals(
            expected = EvaluationResult(IntValue(71)),
            actual = solveProblem(7),
        )
    }

    @Test
    fun testProblem8() {
        val result = solveProblem(8)

        assertEquals(
            expected = EvaluationResult(IntValue(value = 23514624000L)),
            actual = result,
        )
    }

    @Test
    fun testProblem9() {
        val result = solveProblem(9)

        assertIs<EvaluationResult<Value>>(result)

        val dictValue = assertIs<DictValue>(result.value)

        assertEquals<Map<PrimitiveValue, Value>>(
            expected = mapOf(
                Symbol.of("a") to IntValue(value = 1L),
                Symbol.of("b") to IntValue(value = 1L),
                Symbol.of("c") to IntValue(value = 8L),
            ),
            actual = dictValue.entries,
        )
    }

    @Test
    fun testProblem10() {
        assertEquals(
            expected = EvaluationResult(IntValue(value = 4227L)),
            actual = solveProblem(10),
        )
    }

    @Test
    fun testProblem11() {
        assertEquals(
            expected = EvaluationResult(IntValue(value = 70600674L)),
            actual = solveProblem(11),
        )
    }

    @Test
    fun testProblem12() {
        assertEquals(
            expected = EvaluationResult(BoolValue(value = false)),
            actual = solveProblem(12),
        )
    }

    @Test
    fun testProblem13() {
        assertEquals(
            expected = EvaluationResult(BoolValue(value = false)),
            actual = solveProblem(13),
        )
    }

    @Test
    fun testProblem14() {
        assertEquals(
            expected = EvaluationResult(BoolValue(value = false)),
            actual = solveProblem(14),
        )
    }

    @Test
    fun testProblem15() {
        assertEquals(
            expected = EvaluationResult(BoolValue(value = false)),
            actual = solveProblem(15),
        )
    }
}

private fun solveProblem(n: Int): EvaluationOutcome<Value> {
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
        EvaluationStackExhaustionError -> println("Error: call stack exhausted")
        is EvaluationResult -> println("Result: ${evaluationResult.value.dump()}")
    }

    println()

    return result
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

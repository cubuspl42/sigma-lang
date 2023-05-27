package sigma.programs.euler

import sigma.semantics.Project
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.values.tables.DictTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val actual = solveProblem(8)

        assertEquals(
            expected = IntValue(value = 23514624000L),
            actual = actual,
        )
    }

    @Test
    fun testProblem9() {
        val actual = solveProblem(9)

        assertTrue {
            actual.equalsTo(
                DictTable(
                    entries = mapOf(
                        Symbol.of("a") to IntValue(value = 1L),
                        Symbol.of("b") to IntValue(value = 1L),
                        Symbol.of("c") to IntValue(value = 8L),
                    ),
                ),
            )
        }
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
            println(it)
        }
    }

    val result = program.evaluateResult()

    println()
    println("Result:")
    println(result.dump())

    println()

    return result
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

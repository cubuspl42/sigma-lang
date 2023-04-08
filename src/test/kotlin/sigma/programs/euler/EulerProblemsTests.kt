package sigma.programs.euler

import sigma.semantics.Project
import sigma.values.BoolValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.DictTable
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

//        assertEquals(
//            expected = DictTable(
//                entries = mapOf(
//                    Symbol.of("a") to IntValue(value = 1L),
//                    Symbol.of("b") to IntValue(value = 1L),
//                    Symbol.of("c") to IntValue(value = 8L),
//                ),
//            ),
//            actual = actual,
//        )
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

    program.validate()

    val result = program.evaluateResult()

    println(result.dump())

    return result
}

private fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

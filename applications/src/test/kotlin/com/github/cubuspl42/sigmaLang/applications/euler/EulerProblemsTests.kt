package com.github.cubuspl42.sigmaLang.applications.euler

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
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
                Identifier.of("a") to IntValue(value = 1L),
                Identifier.of("b") to IntValue(value = 1L),
                Identifier.of("c") to IntValue(value = 8L),
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

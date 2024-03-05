package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanLiteralTermTest {
    @Test
    fun testFalse() {
        val term = ExpressionTerm.parse(source = "%false")

        assertEquals(
            expected = BooleanLiteralTerm(
                value = BooleanPrimitive.False,
            ),
            actual = term,
        )
    }

    @Test
    fun testTrue() {
        val term = ExpressionTerm.parse(source = "%true")

        assertEquals(
            expected = BooleanLiteralTerm(
                value = BooleanPrimitive.True,
            ),
            actual = term,
        )
    }
}

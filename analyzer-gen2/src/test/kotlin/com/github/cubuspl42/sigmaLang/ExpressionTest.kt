package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.FormExpression
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionTest {
    @Test
    fun testSimple() {
        assertEquals(
            expected = FormExpression(
                entries = listOf(
                    FormExpression.Entry(
                        key = FormExpression.empty,
                        value = FormExpression(
                            entries = listOf(
                                FormExpression.Entry(
                                    key = FormExpression.empty,
                                    value = FormExpression.empty,
                                )
                            ),
                        ),
                    ),
                ),
            ),
            actual = Expression.parse("{{}: {{}: {}}}"),
        )
    }
}

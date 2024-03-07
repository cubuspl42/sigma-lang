package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class IsATermTest {
    @Test
    fun testSimple() {
        val term = ExpressionTerm.parse(
            source = """
                cat %is_a Cat
            """.trimIndent()
        ) as IsATerm

        assertEquals(
            expected = IsATerm(
                instance = ReferenceTerm(
                    referredName = IdentifierTerm(name = "cat"),
                ),
                class_ = ReferenceTerm(
                    referredName = IdentifierTerm(name = "Cat"),
                ),
            ),
            actual = term,
        )
    }
}

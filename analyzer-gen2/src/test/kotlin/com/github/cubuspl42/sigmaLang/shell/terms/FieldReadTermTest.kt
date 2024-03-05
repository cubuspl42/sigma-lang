package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTermTest {
    @Test
    fun testSimple() {
        val term = ExpressionTerm.parse(
            source = """
                a1.a2,
            """.trimIndent()
        ) as FieldReadTerm

        assertEquals(
           actual = term,
            expected = FieldReadTerm(
                subject = ReferenceTerm(
                    referredName = IdentifierTerm(name = "a1"),
                ),
                readFieldName = IdentifierTerm(name = "a2"),
            )
        )

    }
}


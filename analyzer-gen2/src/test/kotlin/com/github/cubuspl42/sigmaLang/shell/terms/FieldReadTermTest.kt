package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.Identifier
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
                readFieldName = Identifier.of(name = "a2"),
            )
        )

    }
}


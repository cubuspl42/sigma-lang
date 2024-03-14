package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleValueConstructorTermTest {
    @Test
    fun testSimple() {
        val term = UnorderedTupleConstructorTerm.parse(
            source = """
                {
                    a1 = {},
                    a2 = {
                        x1 = {},
                    },
                }
            """.trimIndent()
        )

        assertEquals(
            expected = UnorderedTupleConstructorTerm(
                entries = listOf(
                    UnorderedTupleConstructorTerm.Entry(
                        key = IdentifierTerm("a1"),
                        value = UnorderedTupleConstructorTerm.Empty,
                    ),
                    UnorderedTupleConstructorTerm.Entry(
                        key = IdentifierTerm("a2"),
                        value = UnorderedTupleConstructorTerm(
                            entries = listOf(
                                UnorderedTupleConstructorTerm.Entry(
                                    key = IdentifierTerm("x1"),
                                    value = UnorderedTupleConstructorTerm.Empty,
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}

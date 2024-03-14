package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.BooleanValue
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleConstructorTermTest {
    @Test
    fun testSimple() {
        val term = OrderedTupleConstructorTerm.parse(
            source = """
                [
                    a1,
                    a2,
                    {
                        x = %true,
                    },
                ]
            """.trimIndent()
        )

        assertEquals(
            expected = OrderedTupleConstructorTerm(
                elements = listOf(
                    ReferenceTerm(
                        referredName = IdentifierTerm(name = "a1"),
                    ),
                    ReferenceTerm(
                        referredName = IdentifierTerm(name = "a2"),
                    ),
                    UnorderedTupleConstructorTerm(
                        entries = listOf(
                            UnorderedTupleConstructorTerm.Entry(
                                key = IdentifierTerm("x"),
                                value = BooleanLiteralTerm(
                                    value = BooleanValue.True,
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

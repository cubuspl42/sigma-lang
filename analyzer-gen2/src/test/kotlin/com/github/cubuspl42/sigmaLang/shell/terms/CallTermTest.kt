package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class CallTermTest {
    @Test
    fun testSimple() {
        val term = CallTerm.parse(
            source = """
                f1{arg1 = val1, arg2 = {}},
            """.trimIndent()
        )

        assertEquals(
            expected = CallTerm(
                callee = ReferenceTerm(
                    referredName = IdentifierTerm(name = "f1"),
                ),
                passedArgument = UnorderedTupleConstructorTerm(
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("arg1"),
                            value = ReferenceTerm(
                                referredName = IdentifierTerm(name = "val1"),
                            ),
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("arg2"),
                            value = UnorderedTupleConstructorTerm.Empty,
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}

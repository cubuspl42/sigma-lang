package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionConstructorTermTest {
    @Test
    fun testSimple() {
        val term = AbstractionConstructorTerm.parse(
            source = """
                ^{arg3: Type1} => {
                    a1 = {},
                    a2 = arg3,
                },
            """.trimIndent()
        )

        assertEquals(
            expected = AbstractionConstructorTerm(
                argumentType = UnorderedTupleTypeConstructorTerm(
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("arg3"),
                            value = ReferenceTerm(
                                referredName = IdentifierTerm(name = "Type1"),
                            ),
                        ),
                    ),
                ),
                image = UnorderedTupleConstructorTerm(
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("a1"),
                            value = UnorderedTupleConstructorTerm.Empty,
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("a2"),
                            value = ReferenceTerm(
                                referredName = IdentifierTerm(name = "arg3"),
                            ),
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}

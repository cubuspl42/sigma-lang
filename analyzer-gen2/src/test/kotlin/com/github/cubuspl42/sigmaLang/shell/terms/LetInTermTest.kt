package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class LetInTermTest {
    @Test
    fun testSimple() {
        val term = LetInTerm.parse(
            source = """
                %let {
                    foo = ^{arg3: Type1} => {
                        a1 = {},
                        a2 = arg3,
                    },
                    bar = {},
                    baz = {
                        x1 = {},
                    }
                } %in foo2{arg3 = baz}
            """.trimIndent()
        )

        assertEquals(
            expected = LetInTerm(
                block = UnorderedTupleConstructorTerm(
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("foo"),
                            value = AbstractionConstructorTerm(
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
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("bar"),
                            value = UnorderedTupleConstructorTerm.Empty,
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("baz"),
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
                result = CallTerm(
                    callee = ReferenceTerm(
                        referredName = IdentifierTerm(name = "foo2"),
                    ),
                    passedArgument = UnorderedTupleConstructorTerm(
                        entries = listOf(
                            UnorderedTupleConstructorTerm.Entry(
                                key = IdentifierTerm("arg3"),
                                value = ReferenceTerm(
                                    referredName = IdentifierTerm(name = "baz"),
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

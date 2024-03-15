package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class LetInTermTest {
    @Test
    fun testSimple() {
        val term = LetInTerm.parse(
            source = """
                %let {
                    foo = ^{arg3} => {
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
                definitions = setOf(
                    LetInTerm.DefinitionTerm(
                        name = IdentifierTerm("foo"),
                        initializer = AbstractionConstructorTerm(
                            argumentType = UnorderedTupleTypeConstructorTerm(
                                names = setOf(
                                    Identifier.of("arg3"),
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
                    LetInTerm.DefinitionTerm(
                        name = IdentifierTerm("bar"),
                        initializer = UnorderedTupleConstructorTerm.Empty,
                    ),
                    LetInTerm.DefinitionTerm(
                        name = IdentifierTerm("baz"),
                        initializer = UnorderedTupleConstructorTerm(
                            entries = listOf(
                                UnorderedTupleConstructorTerm.Entry(
                                    key = IdentifierTerm("x1"),
                                    value = UnorderedTupleConstructorTerm.Empty,
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

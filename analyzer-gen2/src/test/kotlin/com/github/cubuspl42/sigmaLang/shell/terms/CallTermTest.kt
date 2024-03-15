package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class CallTermTest {
    @Test
    fun testNamedArgs() {
        val term = ExpressionTerm.parse(
            source = """
                f1{arg1 = val1, arg2 = {}},
            """.trimIndent()
        ) as CallTerm

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

    @Test
    fun testUnnamedArgs() {
        val term = ExpressionTerm.parse(
            source = """
                f1[val1, {}],
            """.trimIndent()
        ) as CallTerm

        assertEquals(
            expected = CallTerm(
                callee = ReferenceTerm(
                    referredName = IdentifierTerm(name = "f1"),
                ),
                passedArgument = OrderedTupleConstructorTerm(
                    elements = listOf(
                        ReferenceTerm(
                            referredName = IdentifierTerm(name = "val1"),
                        ),
                        UnorderedTupleConstructorTerm.Empty,
                    ),
                ),
            ),
            actual = term,
        )
    }

    @Test
    fun testFieldReadCallee() {
        val term = ExpressionTerm.parse(
            source = """
                a.b{arg = %true},
            """.trimIndent()
        ) as CallTerm

        assertEquals(
            expected = CallTerm(
                callee = FieldReadTerm(
                    subject = ReferenceTerm(
                        referredName = IdentifierTerm(name = "a"),
                    ),
                    readFieldName = IdentifierTerm(name = "b"),
                ),
                passedArgument = UnorderedTupleConstructorTerm(
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            key = IdentifierTerm("arg"),
                            value = BooleanLiteralTerm.True,
                        ),
                    ),
                ),
            ),
            actual = term,
        )
    }
}

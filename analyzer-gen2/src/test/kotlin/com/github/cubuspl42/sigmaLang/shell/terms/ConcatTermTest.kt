package com.github.cubuspl42.sigmaLang.shell.terms

import kotlin.test.Test
import kotlin.test.assertEquals

class ConcatTermTest {
    @Test
    fun testLists() {
        val term = ExpressionTerm.parse(
            source = """
                foo{} ..l right
            """.trimIndent()
        ) as ConcatTerm

        assertEquals(
            expected = ConcatTerm(
                left = CallTerm(
                    callee = ReferenceTerm(
                        referredName = IdentifierTerm(name = "foo"),
                    ),
                    passedArgument = UnorderedTupleConstructorTerm.Empty,
                ),
                right = ReferenceTerm(
                    referredName = IdentifierTerm(name = "right"),
                ),
                variant = ConcatTerm.Variant.Lists,
            ),
            actual = term,
        )
    }

    @Test
    fun testStrings() {
        val term = ExpressionTerm.parse(
            source = """
                foo{} ..s right
            """.trimIndent()
        ) as ConcatTerm

        assertEquals(
            expected = ConcatTerm(
                left = CallTerm(
                    callee = ReferenceTerm(
                        referredName = IdentifierTerm(name = "foo"),
                    ),
                    passedArgument = UnorderedTupleConstructorTerm.Empty,
                ),
                right = ReferenceTerm(
                    referredName = IdentifierTerm(name = "right"),
                ),
                variant = ConcatTerm.Variant.Strings,
            ),
            actual = term,
        )
    }
}

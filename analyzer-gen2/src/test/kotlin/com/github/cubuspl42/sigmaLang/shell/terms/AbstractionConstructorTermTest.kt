package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.StringValue
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionConstructorTermTest {
    @Test
    fun testNamedArgs() {
        val term = AbstractionConstructorTerm.parse(
            source = """
                ^{arg3: Type1} => "result"
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
                image = StringLiteralTerm(
                    value = StringValue(value = "result"),
                ),
            ),
            actual = term,
        )
    }
}

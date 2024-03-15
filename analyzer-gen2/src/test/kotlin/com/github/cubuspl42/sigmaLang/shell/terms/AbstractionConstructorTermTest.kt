package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.StringValue
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionConstructorTermTest {
    @Test
    fun testNamedArgs() {
        val term = AbstractionConstructorTerm.parse(
            source = """
                ^{arg3} => "result"
            """.trimIndent()
        )

        assertEquals(
            expected = AbstractionConstructorTerm(
                argumentType = UnorderedTupleTypeConstructorTerm(
                    names = setOf(
                        Identifier.of("arg3"),
                    ),
                ),
                image = StringLiteralTerm(
                    value = StringValue(value = "result"),
                ),
            ),
            actual = term,
        )
    }

    @Test
    fun testUnnamedArgs() {
        val term = AbstractionConstructorTerm.parse(
            source = """
                ^[arg1] => "result"
            """.trimIndent()
        )

        assertEquals(
            expected = AbstractionConstructorTerm(
                argumentType = OrderedTupleTypeConstructorTerm(
                    names = listOf(
                        Identifier.of("arg1"),
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

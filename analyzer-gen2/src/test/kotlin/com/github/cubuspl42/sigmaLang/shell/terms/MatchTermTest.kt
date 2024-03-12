package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class MatchTermTest {
    @Test
    fun testSimple() {
        val listResult = "List.length{this = listOrNull}"

        val term = MatchTerm.parse(
            source = """
                %match listOrNull (
                    List => $listResult
                    Nil => "nil"
                )
            """.trimIndent()
        )

        assertEquals(
            expected = MatchTerm(
                matched = ReferenceTerm(
                    referredName = IdentifierTerm(name = "listOrNull"),
                ),
                patternBlocks = listOf(
                    MatchTerm.PatternBlockTerm(
                        class_ = ReferenceTerm(
                            referredName = IdentifierTerm(name = "List"),
                        ),
                        result = ExpressionTerm.parse(listResult)
                    ),
                    MatchTerm.PatternBlockTerm(
                        class_ = ReferenceTerm(
                            referredName = IdentifierTerm(name = "Nil"),
                        ),
                        result = StringLiteralTerm(
                            value = StringPrimitive(
                                value = "nil"
                            ),
                        ),
                    ),
                )
            ),
            actual = term,
        )
    }
}

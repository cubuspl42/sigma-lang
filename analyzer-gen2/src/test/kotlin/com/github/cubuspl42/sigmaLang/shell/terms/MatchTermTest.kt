package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.StringValue
import java.sql.Ref
import kotlin.test.Test
import kotlin.test.assertEquals

class MatchTermTest {
    @Test
    fun testSimple() {
        val term = MatchTerm.parse(
            source = """
                %match animal (
                    Cat %as c => "Cat!"
                    Dog %as d => "Dog!"
                )
            """.trimIndent()
        )

        assertEquals(
            expected = MatchTerm(
                matched = ReferenceTerm(
                    referredName = IdentifierTerm(name = "animal")
                ), patternBlocks = listOf(
                    MatchTerm.CaseTerm(
                        pattern = TagPatternTerm(
                            class_ = ReferenceTerm(referredName = IdentifierTerm(name = "Cat")),
                            newName = Identifier.of("c")
                        ), result = StringLiteralTerm(value = StringValue("Cat!"))
                    ), MatchTerm.CaseTerm(
                        pattern = TagPatternTerm(
                            class_ = ReferenceTerm(referredName = IdentifierTerm(name = "Dog")),
                            newName = Identifier.of(name = "d")
                        ), result = StringLiteralTerm(value = StringValue("Dog!"))
                    )
                )
            ),
            actual = term,
        )
    }
}

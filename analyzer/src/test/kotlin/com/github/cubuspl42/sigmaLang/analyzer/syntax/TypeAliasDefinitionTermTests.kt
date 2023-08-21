package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantDefinitionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = """
                    %const UserId = Int
                """.trimIndent()
            )

            assertEquals(
                expected = ConstantDefinitionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("UserId"),
                    body = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 16),
                        referredName = Symbol.of("Int"),
                    )
                ),
                actual = term,
            )
        }
    }
}
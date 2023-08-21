package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionSourceTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleElement() {
            val expression = ExpressionSourceTerm.parse(
                source = "[a]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceSourceTerm(
                            location = SourceLocation(1, 1),
                            referredName = Symbol.of("a"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleElements() {
            val expression = ExpressionSourceTerm.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceSourceTerm(
                            location = SourceLocation(1, 1),
                            referredName = Symbol.of("a"),
                        ),

                        ReferenceSourceTerm(
                            location = SourceLocation(1, 4),
                            referredName = Symbol.of("b"),
                        ),

                        ReferenceSourceTerm(
                            location = SourceLocation(1, 7),
                            referredName = Symbol.of("c"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }
}
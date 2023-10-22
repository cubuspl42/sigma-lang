@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeSpecificationTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = TypeSpecificationSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Identifier.of("foo"),
                    ),
                    argument = OrderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        elements = listOf(
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referredName = Identifier.of("bar"),
                            ),
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referredName = Identifier.of("baz"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo![bar, baz]"),
            )
        }
    }
}

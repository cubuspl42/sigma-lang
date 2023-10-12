package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Enclosed::class)
class UnionTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val typeExpression = ExpressionSourceTerm.parse(
                source = "A | B | C",
            )

            assertEquals(
                expected = UnionTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    leftType = UnionTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        leftType = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 0),
                            referredName = Identifier.of("A"),
                        ),
                        rightType = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 4),
                            referredName = Identifier.of("B"),
                        ),
                    ),
                    rightType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        referredName = Identifier.of("C"),
                    ),
                ),
                actual = typeExpression,
            )
        }
    }
}

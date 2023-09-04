package com.github.cubuspl42.sigmaLang.analyzer.syntax.typeExpressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorSourceTerm
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
                            referredName = Symbol.of("A"),
                        ),
                        rightType = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 4),
                            referredName = Symbol.of("B"),
                        ),
                    ),
                    rightType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        referredName = Symbol.of("C"),
                    ),
                ),
                actual = typeExpression,
            )
        }
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import utils.ListMatchers
import utils.assertMatches
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "^[n: Int] => 0",
            )

            assertEquals(
                expected = AbstractionConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("n"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referredName = Identifier.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 13),
                        value = IntValue(0),
                    ),
                ),
                actual = term,
            )
        }
    }

    class TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "^[n: Int] => n",
            )

            val expression = Expression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val type = expression.inferredTypeOrIllType.getOrCompute()

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("n")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                        ),
                    ).checked(),
                    imageType = Matcher.Is<IntCollectiveType>(),
                ).checked(),
                actual = type,
            )
        }
    }
}

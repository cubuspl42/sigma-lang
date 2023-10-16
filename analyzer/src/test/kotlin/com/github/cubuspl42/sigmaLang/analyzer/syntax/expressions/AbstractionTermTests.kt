package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypePlaceholder
import utils.assertMatches
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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

        @Test
        fun testGenericWithMultipleParameters() {
            val term = ExpressionSourceTerm.parse(
                source = "!^[a: Type, b: Type] ^[a: a, b: b] => 0",
            )

            assertEquals(
                expected = AbstractionConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    metaArgumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    referredName = Identifier.of("Type"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 15),
                                    referredName = Identifier.of("Type"),
                                ),
                            ),
                        ),
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 21),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 26),
                                    referredName = Identifier.of("a"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 32),
                                    referredName = Identifier.of("b"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 38),
                        value = IntValue(0),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testGenericWithSingleParameter() {
            assertEquals(
                expected = AbstractionConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    metaArgumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("t"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    referredName = Identifier.of("Type"),
                                ),
                            ),
                        ),
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 12),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("n"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                    referredName = Identifier.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 25),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "!^[t: Type] ^[n: Int] => 0",
                ),
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

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Identifier.of("n"),
                                type = IntCollectiveType,
                            ),
                        ),
                    ),
                    imageType = IntCollectiveType,
                ),
                actual = type,
            )
        }

        @Test
        fun testGenericSingleParameter() {
            val term = ExpressionSourceTerm.parse(
                source = "!^[t: Type] ^[t: t] => false",
            )

            val expression = Expression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val type = assertIs<UniversalFunctionType>(
                expression.inferredTypeOrIllType.getOrCompute()
            )

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = Matcher.Is<OrderedTupleType>(
                        OrderedTupleTypeMatcher(
                            elements = listOf(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.Equals(
                                        expected = Identifier.of("t"),
                                    ),
                                    type = Matcher.Is<TypePlaceholder>(),
                                ),
                            ),
                        ),
                    ),
                    imageType = Matcher.Equals(BoolType),
                ),
                actual = type,
            )
        }
    }
}

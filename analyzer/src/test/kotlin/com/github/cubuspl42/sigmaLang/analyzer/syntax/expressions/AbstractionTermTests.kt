package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.OrderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
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
                                name = Symbol.of("n"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referredName = Symbol.of("Int"),
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
                                name = Symbol.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    referredName = Symbol.of("Type"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 15),
                                    referredName = Symbol.of("Type"),
                                ),
                            ),
                        ),
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 21),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 26),
                                    referredName = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 32),
                                    referredName = Symbol.of("b"),
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
                                name = Symbol.of("t"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    referredName = Symbol.of("Type"),
                                ),
                            ),
                        ),
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 12),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("n"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                    referredName = Symbol.of("Int"),
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
                                name = Symbol.of("n"),
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

            val type = expression.inferredTypeOrIllType.getOrCompute()

            assertEquals(
                expected = UniversalFunctionType(
                    metaArgumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("t"),
                                type = TypeType,
                            ),
                        ),
                    ),
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("t"),
                                type = TypeVariable(
                                    formula = Formula.of("t"),
                                ),
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

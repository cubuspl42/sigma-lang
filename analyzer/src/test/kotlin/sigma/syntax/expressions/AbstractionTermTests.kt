package sigma.syntax.expressions

import sigma.semantics.BuiltinScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.SourceLocation
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.semantics.Formula
import sigma.semantics.expressions.Expression
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
                expected = AbstractionSourceTerm(
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
                source = "![a, b] ^[a: a, b: b] => 0",
            )

            assertEquals(
                expected = AbstractionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0), parametersDefinitions = listOf(
                            Symbol.of("a"),
                            Symbol.of("b"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                    referredName = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                    referredName = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 25),
                        value = IntValue(0),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testGenericWithSingleParameter() {
            assertEquals(
                expected = AbstractionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0), parametersDefinitions = listOf(
                            Symbol.of("t"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("n"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referredName = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 18),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "![t] ^[n: Int] => 0",
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
                declarationScope = BuiltinScope,
                term = term,
            )

            val type = expression.inferredType.value

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
                source = "![t] ^[t: t] => false",
            )

            val expression = Expression.build(
                declarationScope = BuiltinScope,
                term = term,
            )

            val type = expression.inferredType.value

            assertEquals(
                expected = UniversalFunctionType(
                    genericParameters = setOf(
                        TypeVariable.of("t"),
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

        @Test
        fun testRecursiveCallTest() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        f = ^[n: Int] -> Bool => f[n + 1]
                    } %in f
                """.trimIndent(),
            )

            val expression = Expression.build(
                declarationScope = BuiltinScope,
                term = term,
            )

            val type = expression.inferredType.value

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
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

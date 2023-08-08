package sigma.syntax.expressions

import sigma.semantics.BuiltinScope
import sigma.syntax.expressions.ReferenceTerm
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
            val term = ExpressionTerm.parse(
                source = "^[n: Int] => 0",
            )

            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("n"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 13),
                        value = IntValue(0),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testGenericWithMultipleParameters() {
            val term = ExpressionTerm.parse(
                source = "![a, b] ^[a: a, b: b] => 0",
            )

            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0), parametersDefinitions = listOf(
                            Symbol.of("a"),
                            Symbol.of("b"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("a"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                    referee = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
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
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0), parametersDefinitions = listOf(
                            Symbol.of("t"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("n"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 18),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "![t] ^[n: Int] => 0",
                ),
            )
        }
    }

    class TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
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
            val term = ExpressionTerm.parse(
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
            val term = ExpressionTerm.parse(
                source = """
                    let {
                        f = ^[n: Int] -> Bool => f[n + 1]
                    } in f
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

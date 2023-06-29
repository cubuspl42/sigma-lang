package sigma.syntax.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.SyntaxValueScope
import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm.GenericParametersTuple
import sigma.syntax.typeExpressions.OrderedTupleTypeConstructorTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import sigma.semantics.DeclarationScope
import sigma.semantics.expressions.Expression
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("n"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 12),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "[n: Int] => 0",
                ),
            )
        }

        @Test
        fun testGenericWithMultipleParameters() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("a"),
                            Symbol.of("b"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                    referee = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("b"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 18),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 24),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "![a, b] [a: a, b: b] => 0",
                ),
            )
        }

        @Test
        fun testGenericWithSingleParameter() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("t"),
                        )
                    ),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("n"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 17),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "![t] [n: Int] => 0",
                ),
            )
        }
    }

    object TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
                source = "[n: Int] => n",
            )

            val expression = Expression.build(
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
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
                source = "![t] [t: t] => false",
            )

            val expression = Expression.build(
                typeScope = BuiltinTypeScope,
                declarationScope = BuiltinScope,
                term = term,
            )

            val type = expression.inferredType.value

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("t"),
                                type = TypeVariable(
                                    name = Symbol.of("t"),
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
                        f = [n: Int] -> Bool => f[n + 1]
                    } in f
                """.trimIndent(),
            )

            val expression = Expression.build(
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
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

    object EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstraction = ExpressionTerm.parse(
                source = "[n: Int, m: Int] => n * m",
            ) as AbstractionTerm

            val closure = abstraction.evaluate(
                scope = BuiltinScope,
            )

            assertEquals(
                expected = IntValue(6),
                actual = closure.apply(
                    ArrayTable(
                        elements = listOf(
                            IntValue(2),
                            IntValue(3),
                        ),
                    ),
                ),
            )
        }
    }
}

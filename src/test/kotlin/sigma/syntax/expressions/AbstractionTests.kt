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
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteralTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeLiteralTerm.Element(
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
                    argumentType = OrderedTupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        elements = listOf(
                            OrderedTupleTypeLiteralTerm.Element(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                    referee = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeLiteralTerm.Element(
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
                    argumentType = OrderedTupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeLiteralTerm.Element(
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
                actual = ExpressionTerm.parse(
                    source = "[n: Int] => n",
                ).determineType(
                    typeScope = BuiltinTypeScope,
                    valueScope = SyntaxValueScope.Empty,
                ),
            )
        }

        @Test
        fun testGenericSingleParameter() {
            val type = ExpressionTerm.parse(
                source = "![t] [t: t] => false",
            ).determineType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("t"),
                                type = TypeVariable,
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
            val type = ExpressionTerm.parse(
                source = """
                    let {
                        f = [n: Int] -> Bool => f[n + 1]
                    } in f
                """.trimIndent(),
            ).determineType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

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

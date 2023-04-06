package sigma.syntax.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.StaticValueScope
import sigma.TypeReference
import sigma.syntax.expressions.Abstraction.GenericParametersTuple
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteral
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.ArrayTable
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("n"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteral(
                        SourceLocation(lineIndex = 1, columnIndex = 12),
                        value = IntValue(0),
                    ),
                ),
                actual = Expression.parse(
                    source = "[n: Int] => 0",
                ),
            )
        }

        @Test
        fun testGenericWithMultipleParameters() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("a"),
                            Symbol.of("b"),
                        )
                    ),
                    argumentType = OrderedTupleTypeLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        elements = listOf(
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("a"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                    referee = Symbol.of("a"),
                                ),
                            ),
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("b"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 18),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteral(
                        SourceLocation(lineIndex = 1, columnIndex = 24),
                        value = IntValue(0),
                    ),
                ),
                actual = Expression.parse(
                    source = "![a, b] [a: a, b: b] => 0",
                ),
            )
        }


        @Test
        fun testGenericWithSingleParameter() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("t"),
                        )
                    ),
                    argumentType = OrderedTupleTypeLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("n"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteral(
                        SourceLocation(lineIndex = 1, columnIndex = 17),
                        value = IntValue(0),
                    ),
                ),
                actual = Expression.parse(
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
                actual = Expression.parse(
                    source = "[n: Int] => n",
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = StaticValueScope.Empty,
                ),
            )
        }

        @Test
        fun testGenericSingleParameter() {
            val type = Expression.parse(
                source = "![t] [t: t] => false",
            ).validateAndInferType(
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
    }

    object EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstraction = Expression.parse(
                source = "[n: Int, m: Int] => n * m",
            ) as Abstraction

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

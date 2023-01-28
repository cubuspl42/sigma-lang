package sigma.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.StaticValueScope
import sigma.TypeReference
import sigma.expressions.Abstraction.MetaArgumentExpression
import sigma.typeExpressions.OrderedTupleTypeLiteral
import sigma.types.AbstractionType
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.MetaType
import sigma.types.OrderedTupleType
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
        fun testWithMetaArgument() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    metaArgument = MetaArgumentExpression(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        name = Symbol.of("t"),
                    ),
                    argumentType = OrderedTupleTypeLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        elements = listOf(
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("n"),
                                type = TypeReference(
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
                expected = AbstractionType(
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
        fun testWithMetaArgument() {
            val type = Expression.parse(
                source = "![t] [n: Int] => false",
            ).validateAndInferType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = AbstractionType(
                    metaArgumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("t"),
                                type = MetaType,
                            ),
                        ),
                    ),
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

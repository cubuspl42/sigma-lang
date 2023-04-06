package sigma.syntax.type_expressions

import sigma.BuiltinTypeScope
import sigma.StaticTypeScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.TypeReference
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.FunctionTypeDepiction
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteral
import sigma.semantics.types.ArrayType
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeDepictionTests {
    object ParsingTests {
        @Test
        fun test() {
            val typeExpression = TypeExpression.parse(
                source = "[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeDepiction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("a"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeLiteral.Element(
                                name = Symbol.of("b"),
                                type = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = TypeReference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 16),
                        referee = Symbol.of("C"),
                    ),
                ),
                actual = typeExpression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val type = TypeExpression.parse(
                source = "[a: Int, b: Bool] -> Bool",
            ).evaluate(
                typeScope = BuiltinTypeScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("a"),
                                type = IntCollectiveType,
                            ),
                            OrderedTupleType.Element(
                                name = Symbol.of("b"),
                                type = BoolType,
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

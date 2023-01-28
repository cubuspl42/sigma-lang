package sigma.expressions

import sigma.TypeExpression
import sigma.TypeReference
import sigma.types.ArrayType
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.OrderedTupleType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpression.parse(
                source = "[A*]",
            )

            assertEquals(
                expected = ArrayTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = TypeReference(
                        referee = Symbol.of("A"),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val type = TypeExpression.parse(
                source = "[A*]",
            ).evaluate(
                typeScope = FixedStaticTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                    ),
                ),
            )

            assertEquals(
                expected = ArrayType(
                    elementType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

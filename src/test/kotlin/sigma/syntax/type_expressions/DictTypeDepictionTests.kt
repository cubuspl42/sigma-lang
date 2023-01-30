package sigma.syntax.type_expressions

import sigma.BuiltinTypeScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.TypeReference
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.ArrayTypeLiteral
import sigma.syntax.typeExpressions.DictTypeDepiction
import sigma.types.ArrayType
import sigma.types.BoolType
import sigma.types.DictType
import sigma.types.IntCollectiveType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeDepictionTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpression.parse(
                source = "{[K]: V}",
            )

            assertEquals(
                expected = DictTypeDepiction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    keyType = TypeReference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referee = Symbol.of("K"),
                    ),
                    valueType = TypeReference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 6),
                        referee = Symbol.of("V"),
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
                source = "{[Int]: Bool}",
            ).evaluate(
                typeScope = BuiltinTypeScope,
            )

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

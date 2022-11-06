package sigma.expressions

import sigma.GlobalStaticScope
import sigma.TypeReference
import sigma.types.AbstractionType
import sigma.types.IntCollectiveType
import sigma.types.IntLiteralType
import sigma.types.UndefinedType
import sigma.values.IntValue
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentName = Symbol.of("n"),
                    argumentType = TypeReference(
                        referee = Symbol.of("Int"),
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
    }

    object TypeCheckingTests {
        @Test
        fun test() {
            assertEquals(
                expected = AbstractionType(
                    argumentType = IntCollectiveType,
                    imageType = IntCollectiveType,
                ),
                actual = Expression.parse(
                    source = "[n: Int] => n",
                ).inferType(
                    scope = GlobalStaticScope,
                ),
            )
        }

        @Test
        fun testUnannotatedArgument() {
            assertEquals(
                expected = AbstractionType(
                    argumentType = UndefinedType,
                    imageType = IntLiteralType(IntValue.Zero),
                ),
                actual = Expression.parse(
                    source = "[a] => 0",
                ).inferType(
                    scope = GlobalStaticScope,
                ),
            )
        }
    }

    object EvaluationTests {}
}

package sigma.expressions

import sigma.GlobalStaticScope
import sigma.TypeReference
import sigma.expressions.Abstraction.MetaArgumentExpression
import sigma.types.AbstractionType
import sigma.types.ArrayType
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.IntLiteralType
import sigma.types.MetaType
import sigma.types.UndefinedType
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.DictTable
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

        @Test
        fun testWithMetaArgument() {
            assertEquals(
                expected = Abstraction(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    metaArgument = MetaArgumentExpression(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        name = Symbol.of("t"),
                    ),
                    argumentName = Symbol.of("n"),
                    argumentType = TypeReference(
                        referee = Symbol.of("Int"),
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

        @Test
        fun testWithMetaArgument() {
            val type = Expression.parse(
                source = "![t] [n: Int] => false",
            ).validateAndInferType(
                scope = GlobalStaticScope,
            )

            assertEquals(
                expected = AbstractionType(
                    // TODO: Improve array typing
                    metaArgumentType = ArrayType(
                        elementType = MetaType,
                    ),
                    argumentType = IntCollectiveType,
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }

    object EvaluationTests {}
}

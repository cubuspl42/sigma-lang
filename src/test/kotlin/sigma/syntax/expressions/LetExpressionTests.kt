package sigma.syntax.expressions

import org.junit.jupiter.api.assertThrows
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.TypeReference
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class LetExpressionTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = LetExpression(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScope(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        declarations = listOf(
                            Declaration(
                                name = Symbol.of("g"),
                                value = Call(
                                    location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                    subject = Reference(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                        referee = Symbol.of("h"),
                                    ),
                                    argument = Reference(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                                        referee = Symbol.of("a"),
                                    ),
                                ),
                            ),
                            Declaration(
                                name = Symbol.of("f"),
                                value = Reference(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    referee = Symbol.of("g"),
                                ),
                            ),
                        ),
                    ),
                    result = Call(
                        location = SourceLocation(lineIndex = 4, columnIndex = 5),
                        subject = Reference(
                            location = SourceLocation(lineIndex = 4, columnIndex = 5),
                            referee = Symbol.of("f"),
                        ),
                        argument = Reference(
                            location = SourceLocation(lineIndex = 4, columnIndex = 7),
                            referee = Symbol.of("x"),
                        ),
                    ),
                ),
                actual = Expression.parse(
                    source = """
                        let {
                            g = h(a),
                            f = g,
                        } in f(x)
                    """.trimIndent()
                ),
            )
        }

        @Test
        fun testWithTypeAnnotation() {
            assertEquals(
                expected = LetExpression(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScope(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        declarations = listOf(
                            Declaration(
                                name = Symbol.of("a"),
                                valueType = TypeReference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    referee = Symbol.of("Int"),
                                ),
                                value = Reference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 15),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    result = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 22),
                        referee = Symbol.of("a"),
                    ),
                ),
                actual = Expression.parse(
                    source = """
                        let { a: Int = b } in a
                    """.trimIndent()
                ),
            )
        }
    }

    object TypeCheckingTests {
        @Test
        fun testInferred() {
            val type = Expression.parse(
                source = """
                    let {
                        a: Bool = false,
                        b = a,
                    } in b
                """.trimIndent()
            ).validateAndInferType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = BoolType,
                actual = type,
            )
        }

        @Test
        fun testInferredFunctionType() {
            val type = Expression.parse(
                source = """
                    let {
                        f = [n: Int] => false,
                        a = f[0],
                    } in a
                """.trimIndent()
            ).validateAndInferType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = BoolType,
                actual = type,
            )
        }

        @Test
        fun testAssignment() {
            val expression = Expression.parse(
                source = """
                    let {
                        a: Int = 0,
                    } in a
                """.trimIndent()
            )

            expression.validate(
                typeScope = BuiltinTypeScope,
                valueScope = StaticValueScope.Empty,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = expression.inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = StaticValueScope.Empty,
                ),
            )
        }

        @Test
        fun testCyclic() {
            // TODO: Improve this
            assertThrows<StackOverflowError> {
                Expression.parse(
                    source = """
                        let {
                            a = b,
                            b = a,
                        } in a
                    """.trimIndent()
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = StaticValueScope.Empty,
                )
            }
        }
    }
}

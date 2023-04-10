package sigma.syntax.expressions

import org.junit.jupiter.api.assertThrows
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.TypeReferenceTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.syntax.DeclarationTerm
import sigma.syntax.SourceLocation
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class LetExpressionTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = LetExpressionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        declarations = listOf(
                            DeclarationTerm(
                                location = SourceLocation(lineIndex = 2, columnIndex = 4),
                                name = Symbol.of("g"),
                                value = CallTerm(
                                    location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                    subject = ReferenceTerm(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                        referee = Symbol.of("h"),
                                    ),
                                    argument = ReferenceTerm(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                                        referee = Symbol.of("a"),
                                    ),
                                ),
                            ),
                            DeclarationTerm(
                                location = SourceLocation(lineIndex = 3, columnIndex = 4),
                                name = Symbol.of("f"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    referee = Symbol.of("g"),
                                ),
                            ),
                        ),
                    ),
                    result = CallTerm(
                        location = SourceLocation(lineIndex = 4, columnIndex = 5),
                        subject = ReferenceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 5),
                            referee = Symbol.of("f"),
                        ),
                        argument = ReferenceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 7),
                            referee = Symbol.of("x"),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse(
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
                expected = LetExpressionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        declarations = listOf(
                            DeclarationTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                name = Symbol.of("a"),
                                valueType = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    referee = Symbol.of("Int"),
                                ),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 15),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    result = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 22),
                        referee = Symbol.of("a"),
                    ),
                ),
                actual = ExpressionTerm.parse(
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
            val type = ExpressionTerm.parse(
                source = """
                    let {
                        a: Bool = false,
                        b = a,
                    } in b
                """.trimIndent()
            ).determineType(
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
            val type = ExpressionTerm.parse(
                source = """
                    let {
                        f = [n: Int] => false,
                        a = f[0],
                    } in a
                """.trimIndent()
            ).determineType(
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
            val expression = ExpressionTerm.parse(
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
                actual = expression.determineType(
                    typeScope = BuiltinTypeScope,
                    valueScope = StaticValueScope.Empty,
                ),
            )
        }

        @Test
        fun testCyclic() {
            // TODO: Improve this
            assertThrows<StackOverflowError> {
                ExpressionTerm.parse(
                    source = """
                        let {
                            a = b,
                            b = a,
                        } in a
                    """.trimIndent()
                ).determineType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = StaticValueScope.Empty,
                )
            }
        }
    }
}

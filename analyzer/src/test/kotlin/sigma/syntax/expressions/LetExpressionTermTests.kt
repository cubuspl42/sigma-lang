package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.LocalDefinitionSourceTerm
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class LetExpressionTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        g = h(a),
                        f = g,
                    } %in f(x)
                """.trimIndent()
            )

            assertEquals(
                expected = LetExpressionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        definitions = listOf(
                            LocalDefinitionSourceTerm(
                                location = SourceLocation(lineIndex = 2, columnIndex = 4),
                                name = Symbol.of("g"),
                                body = CallSourceTerm(
                                    location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                    subject = ReferenceSourceTerm(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 8),
                                        referee = Symbol.of("h"),
                                    ),
                                    argument = ReferenceSourceTerm(
                                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                                        referee = Symbol.of("a"),
                                    ),
                                ),
                            ),
                            LocalDefinitionSourceTerm(
                                location = SourceLocation(lineIndex = 3, columnIndex = 4),
                                name = Symbol.of("f"),
                                body = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    referee = Symbol.of("g"),
                                ),
                            ),
                        ),
                    ),
                    result = CallSourceTerm(
                        location = SourceLocation(lineIndex = 4, columnIndex = 6),
                        subject = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 6),
                            referee = Symbol.of("f"),
                        ),
                        argument = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 8),
                            referee = Symbol.of("x"),
                        ),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testWithTypeAnnotation() {
            val term = ExpressionSourceTerm.parse(
                source = """
                        %let { a: Int = b } %in a
                    """.trimIndent()
            )

            assertEquals(
                expected = LetExpressionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        definitions = listOf(
                            LocalDefinitionSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                name = Symbol.of("a"),
                                declaredTypeBody = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("Int"),
                                ),
                                body = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 16),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    result = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 24),
                        referee = Symbol.of("a"),
                    ),
                ),
                actual = term,
            )
        }
    }
}

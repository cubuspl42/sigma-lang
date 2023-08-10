package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.LocalDefinitionTerm
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class LetExpressionTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = ExpressionTerm.parse(
                source = """
                    %let {
                        g = h(a),
                        f = g,
                    } %in f(x)
                """.trimIndent()
            )

            assertEquals(
                expected = LetExpressionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        definitions = listOf(
                            LocalDefinitionTerm(
                                location = SourceLocation(lineIndex = 2, columnIndex = 4),
                                name = Symbol.of("g"),
                                body = CallTerm(
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
                            LocalDefinitionTerm(
                                location = SourceLocation(lineIndex = 3, columnIndex = 4),
                                name = Symbol.of("f"),
                                body = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    referee = Symbol.of("g"),
                                ),
                            ),
                        ),
                    ),
                    result = CallTerm(
                        location = SourceLocation(lineIndex = 4, columnIndex = 6),
                        subject = ReferenceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 6),
                            referee = Symbol.of("f"),
                        ),
                        argument = ReferenceTerm(
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
            val term = ExpressionTerm.parse(
                source = """
                        %let { a: Int = b } %in a
                    """.trimIndent()
            )

            assertEquals(
                expected = LetExpressionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    localScope = LocalScopeTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        definitions = listOf(
                            LocalDefinitionTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                name = Symbol.of("a"),
                                declaredTypeBody = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("Int"),
                                ),
                                body = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 16),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                    ),
                    result = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 24),
                        referee = Symbol.of("a"),
                    ),
                ),
                actual = term,
            )
        }
    }
}

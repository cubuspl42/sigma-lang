package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.DefinitionTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TypeReferenceTerm
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
                        definitions = listOf(
                            DefinitionTerm(
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
                            DefinitionTerm(
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
                        definitions = listOf(
                            DefinitionTerm(
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
}

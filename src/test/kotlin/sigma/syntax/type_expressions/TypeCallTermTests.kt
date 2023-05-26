package sigma.syntax.type_expressions

import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.metaExpressions.MetaCallTerm
import sigma.syntax.metaExpressions.UnorderedTupleTypeLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeCallTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = MetaExpressionTerm.parse(
                source = "Foo[Bar, {a: Int, b: Bool}]",
            )

            assertEquals(
                expected = MetaCallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    callee = MetaReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("Foo"),
                    ),
                    passedArgument = MetaCallTerm.TypeTupleLiteral(
                        elements = listOf(
                            MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("Bar"),
                            ),
                            UnorderedTupleTypeLiteralTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                entries = listOf(
                                    UnorderedTupleTypeLiteralTerm.Entry(
                                        name = Symbol.of("a"),
                                        valueType = MetaReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                    UnorderedTupleTypeLiteralTerm.Entry(
                                        name = Symbol.of("b"),
                                        valueType = MetaReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 21),
                                            referee = Symbol.of("Bool"),
                                        ),
                                    ),
                                ),
                            )
                        ),
                    ),
                ),
                actual = term,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val term = MetaExpressionTerm.parse(
                source = "Foo[Bar]",
            )
        }
    }
}

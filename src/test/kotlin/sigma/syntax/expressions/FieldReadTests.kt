package sigma.syntax.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.DictTable
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldReadTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = ExpressionTerm.parse("foo.bar"),
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testSimple() {
            val foo = DictTable(
                entries = mapOf(
                    Symbol.of("bar") to IntValue(value = 123L),
                ),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = ExpressionTerm.parse("foo.bar").evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("foo") to foo,
                        )
                    ),
                ),
            )
        }
    }
}

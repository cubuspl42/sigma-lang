package sigma.syntax.expressions

import sigma.BuiltinTypeScope
import sigma.SyntaxValueScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedTermCheckTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IsUndefinedCheckTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argument = ReferenceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 12),
                        referee = Symbol.of("foo"),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "isUndefined foo",
                ),
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testNotUndefined() {
            assertEquals(
                expected = BoolValue.False,
                actual = ExpressionTerm.parse(
                    source = "isUndefined 0",
                ).evaluate(
                    scope = Scope.Empty,
                ),
            )
        }

        @Test
        fun testUndefined() {
            assertEquals(
                expected = BoolValue.True,
                actual = ExpressionTerm.parse(
                    source = "isUndefined d(0)",
                ).evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("d") to DictTable.Empty,
                        ),
                    ),
                ),
            )
        }
    }
}

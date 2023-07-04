package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.DictTable
import sigma.semantics.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedCheckTests {
    object TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
                source = "isUndefined foo",
            ) as IsUndefinedCheckTerm

            val isUndefinedCheck = IsUndefinedCheck.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            assertEquals(
                expected = BoolType,
                actual = isUndefinedCheck.inferredType.value,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testNotUndefined() {
            val isUndefinedCheck = IsUndefinedCheck.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = ExpressionTerm.parse(
                    source = "isUndefined 0",
                ) as IsUndefinedCheckTerm,
            )

            assertEquals(
                expected = BoolValue.False,
                actual = isUndefinedCheck.evaluate(
                    scope = Scope.Empty,
                ),
            )
        }

        @Test
        fun testUndefined() {
            val dictTable = DictTable.Empty

            val isUndefinedCheck = IsUndefinedCheck.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = ExpressionTerm.parse(
                    source = "isUndefined d(0)",
                ) as IsUndefinedCheckTerm,
            )

            assertEquals(
                expected = BoolValue.True,
                actual = isUndefinedCheck.evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("d") to dictTable,
                        ),
                    ),
                ),
            )
        }
    }
}

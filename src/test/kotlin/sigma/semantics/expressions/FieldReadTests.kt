package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.semantics.StaticScope
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FieldReadTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FieldReadTests {

    class EvaluationTests {
        @Test
        fun testSimple() {
            val foo = DictValue(
                entries = mapOf(
                    Symbol.of("bar") to IntValue(value = 123L),
                ),
            )

            val fieldRead = FieldRead.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse("foo.bar") as FieldReadTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                fieldRead.bind(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("foo") to foo,
                        )
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = result.value,
            )
        }
    }

}

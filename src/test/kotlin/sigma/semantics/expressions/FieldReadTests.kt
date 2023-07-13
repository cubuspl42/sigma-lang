package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.DictValue
import sigma.semantics.DeclarationScope
import sigma.semantics.TypeScope
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FieldReadTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTests {


    object EvaluationTests {
        @Test
        fun testSimple() {
            val foo = DictValue(
                entries = mapOf(
                    Symbol.of("bar") to IntValue(value = 123L),
                ),
            )

            val fieldRead = FieldRead.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = ExpressionTerm.parse("foo.bar") as FieldReadTerm,
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = fieldRead.evaluate(
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

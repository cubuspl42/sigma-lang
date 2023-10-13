package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadSourceTerm
import utils.FakeDefinition
import utils.FakeStaticBlock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FieldReadTests {

    class EvaluationTests {
        @Test
        fun testSimple() {
            val fooValue = DictValue.fromMap(
                entries = mapOf(
                    Identifier.of("bar") to IntValue(value = 123L),
                ),
            )

            val term = ExpressionSourceTerm.parse("foo.bar") as FieldReadSourceTerm
            val fieldRead = FieldRead.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("foo"),
                            type = NeverType,
                            value = fooValue,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                fieldRead.bind(
                    dynamicScope = DynamicScope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = result.value,
            )
        }
    }

}

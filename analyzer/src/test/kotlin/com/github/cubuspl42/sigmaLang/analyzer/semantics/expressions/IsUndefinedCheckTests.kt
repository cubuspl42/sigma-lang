package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckSourceTerm
import utils.FakeDefinition
import utils.FakeStaticScope
import utils.FakeUserDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IsUndefinedCheckTests {
    class TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "%isUndefined foo",
            ) as IsUndefinedCheckSourceTerm

            val isUndefinedCheck = IsUndefinedCheck.build(
                context = Expression.BuildContext(
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("foo"),
                            declaredType = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = BoolType,
                actual = isUndefinedCheck.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testNotUndefined() {
            val isUndefinedCheck = IsUndefinedCheck.build(
                context = Expression.BuildContext.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "%isUndefined 0",
                ) as IsUndefinedCheckSourceTerm,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                isUndefinedCheck.bind(
                    dynamicScope = DynamicScope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = BoolValue.False,
                actual = result.value,
            )
        }

        @Test
        fun testUndefined() {
            val dictValue = DictValue.Empty

            val term = ExpressionSourceTerm.parse(
                source = "%isUndefined d(0)",
            ) as IsUndefinedCheckSourceTerm

            val isUndefinedCheck = IsUndefinedCheck.build(
                context = Expression.BuildContext(
                    outerScope = FakeStaticScope.of(
                        FakeDefinition(
                            name = Identifier.of("d"),
                            type = NeverType,
                            value = dictValue,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                isUndefinedCheck.bind(
                    dynamicScope = DynamicScope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = BoolValue.True,
                actual = result.value,
            )
        }
    }
}

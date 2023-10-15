package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeAnnotatedBody
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Enclosed::class)
class LocalDefinitionTests {
    class TypeCheckingTests {
        @Test
        fun testUnmatchedInferredType() {
            val letExpressionTerm = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        a: Int = false
                    } %in a
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val definitionTerm = letExpressionTerm.localScope.definitions.single()

            val definition = UserVariableDefinition.build(
                context = Expression.BuildContext.Builtin,
                term = definitionTerm,
            )

            assertEquals(
                expected = setOf(
                    TypeAnnotatedBody.UnmatchedInferredTypeError(
                        location = null,
                        matchResult = MembershipType.TotalMismatch(
                            expectedType = IntCollectiveType,
                            actualType = BoolType,
                        ),
                    ),
                ),
                actual = definition.errors,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = definition.computedBodyType.getOrCompute(),
            )
        }

    }
}

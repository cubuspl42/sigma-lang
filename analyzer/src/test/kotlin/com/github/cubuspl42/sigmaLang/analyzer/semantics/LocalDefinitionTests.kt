package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.TypeAnnotatedBody
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.UserVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
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
                        matchResult = SpecificType.TotalMismatch(
                            expectedType = IntCollectiveType,
                            actualType = BoolType,
                        ),
                    ),
                ),
                actual = definition.errors,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = definition.body.inferredTypeOrIllType.getOrCompute(),
            )
        }

    }
}

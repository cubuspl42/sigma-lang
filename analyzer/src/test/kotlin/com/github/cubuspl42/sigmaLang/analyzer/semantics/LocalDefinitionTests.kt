package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
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
                declarationScope = BuiltinScope,
                term = definitionTerm,
            )

            assertEquals(
                expected = setOf(
                    UserDefinition.UnmatchedInferredTypeError(
                        location = SourceLocation(lineIndex = 2, columnIndex = 13),
                        matchResult = Type.TotalMismatch(
                            expectedType = IntCollectiveType,
                            actualType = BoolType,
                        ),
                    ),
                ),
                actual = definition.errors,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = definition.declaredType.value,
            )
        }

    }
}

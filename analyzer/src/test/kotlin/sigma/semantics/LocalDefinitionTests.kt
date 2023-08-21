package sigma.semantics

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.LetExpressionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

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

            val definition = LocalValueDefinition.build(
                declarationScope = BuiltinScope,
                term = definitionTerm,
            )

            assertEquals(
                expected = setOf(
                    ValueDefinition.UnmatchedInferredTypeError(
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
                actual = definition.effectiveValueType.value,
            )
        }

    }
}

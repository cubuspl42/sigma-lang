package sigma.semantics

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.LetExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDefinitionTests {
    object TypeCheckingTests {
        @Test
        fun testUnmatchedInferredType() {
            val letExpressionTerm = ExpressionTerm.parse(
                source = """
                    let {
                        a: Int = false
                    } in a
                """.trimIndent(),
            ) as LetExpressionTerm

            val definitionTerm = letExpressionTerm.localScope.definitions.single()

            val definition = LocalValueDefinition.build(
                typeScope = BuiltinTypeScope,
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

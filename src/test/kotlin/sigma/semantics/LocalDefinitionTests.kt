package sigma.semantics

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
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

            val definition = LocalDefinition.build(
                typeScope = BuiltinTypeScope,
                declarationScope = BuiltinScope,
                term = definitionTerm,
            )

            assertEquals(
                expected = setOf(
                    Definition.UnmatchedInferredTypeError(
                        declaredType = IntCollectiveType,
                        inferredType = BoolType,
                    ),
                ),
                actual = definition.errors,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = definition.inferredType.value,
            )
        }

    }
}

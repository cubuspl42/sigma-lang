package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.NamespaceDefinitionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object NamespaceTests {
    object TypeCheckingTests {
        @Test
        fun testInnerTypeScope() {
            val term = NamespaceDefinitionTerm.parse(
                source = """
                    namespace Foo (
                        const UserId = Int
                        
                        const isUserIdValid = ^[userId: UserId] => true
                    )
                """.trimIndent(),
            )

            val namespace = Namespace.build(
                prelude = Prelude.load(),
                term = term,
            )

            val isUserIdValidDefinition = namespace.getConstantDefinition(
                name = Symbol.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValidDefinition)

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("userId"),
                                type = IntCollectiveType,
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = isUserIdValidDefinition.asValueDefinition.effectiveValueType.value,
            )
        }
    }
}

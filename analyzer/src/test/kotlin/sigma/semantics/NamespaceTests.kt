package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.NamespaceDefinitionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NamespaceTests {
    class TypeCheckingTests {
        @Test
        fun testInnerTypeScope() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %const UserId = Int
                        
                        %const isUserIdValid = ^[userId: UserId] => true
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

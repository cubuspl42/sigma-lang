package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.ModuleSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModuleTests {
    class TypeCheckingTests {
        @Test
        fun testTypeScope() {
            val term = ModuleSourceTerm.parse(
                source = """
                    %const UserId = Int
                    
                    %const isUserIdValid = ^[userId: UserId] => true
                """.trimIndent(),
            )

            val module = Module.build(
                prelude = Prelude.load(),
                term = term,
            )

            val isUserIdValid = module.rootNamespace.getConstantDefinition(
                name = Symbol.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValid)

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
                actual = isUserIdValid.asValueDefinition.effectiveValueType.value,
            )
        }
    }
}

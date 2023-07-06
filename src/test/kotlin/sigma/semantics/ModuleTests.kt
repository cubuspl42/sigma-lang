package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.ModuleTerm
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.LetExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object ModuleTests {
    object TypeCheckingTests {
        @Test
        fun testTypeScope() {
            val term = ModuleTerm.parse(
                source = """
                    typeAlias UserId = Int
                    
                    const isUserIdValid = [userId: UserId] => true
                """.trimIndent(),
            )

            val module = Module.build(
                prelude = Prelude.load(),
                term = term,
            )

            val isUserIdValid = module.getConstantDefinition(name = Symbol.of("isUserIdValid"))

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
                actual = isUserIdValid.asValueDefinition.inferredType.value,
            )
        }
    }
}

package sigma.semantics.expressions

import sigma.BuiltinTypeScope
import sigma.Computation
import sigma.semantics.Declaration
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.IntType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.values.Symbol
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertIs

class CallTests {
    object TypeCheckingTests {

        @Test
        fun testFunctionCall() {
            val term = ExpressionTerm.parse(
                source = "f[1]",
            ) as CallTerm

            val call = Call.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "f" to UniversalFunctionType(
                            argumentType = OrderedTupleType(
                                elements = listOf(
                                    OrderedTupleType.Element(
                                        name = null,
                                        // Note: passes argument is an integer, but it doesn't matter for type
                                        // inference
                                        type = BoolType,
                                    ),
                                ),
                            ),
                            imageType = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            )

            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalCall() {
            val term = ExpressionTerm.parse(
                source = "b[1]",
            ) as CallTerm

            val call = Call.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "b" to BoolType,
                    ),
                ),
                term = term,
            )

            assertIs<IllType>(
                value = call.inferredType.value,
            )
        }
    }
}

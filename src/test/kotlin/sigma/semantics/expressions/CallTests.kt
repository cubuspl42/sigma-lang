package sigma.semantics.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.IntType
import sigma.semantics.types.TupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.ExpressionTerm
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CallTests {
    object TypeCheckingTests {
        @Test
        fun testLegalSubject() {
            val term = ExpressionTerm.parse(
                source = "f[false]",
            ) as CallTerm

            val call = Call.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "f" to UniversalFunctionType(
                            argumentType = TupleType.ordered(
                                TupleType.OrderedEntry(
                                    index = 0,
                                    name = null,
                                    type = BoolType,
                                ),
                            ),
                            imageType = IntCollectiveType,
                        ),
                    ),
                ).chainWith(
                    outerScope = BuiltinScope,
                ),
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                actual = call.errors,
            )

            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalOrderedArgument() {
            val term = ExpressionTerm.parse(
                source = "f[1]",
            ) as CallTerm

            val call = Call.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "f" to UniversalFunctionType(
                            argumentType = TupleType.ordered(
                                TupleType.OrderedEntry(
                                    index = 0,
                                    name = Symbol.of("a"),
                                    type = BoolType,
                                ),
                            ),
                            imageType = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = call.errors,
                actual = setOf(
                    Call.InvalidArgumentError,
                ),
            )

            // Wrong argument does not affect the inferred type
            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalSubject() {
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

            assertEquals(
                expected = setOf(
                    Call.IllegalSubjectCallError(
                        illegalSubjectType = BoolType,
                    ),
                ),
                actual = call.errors,
            )
        }
    }
}

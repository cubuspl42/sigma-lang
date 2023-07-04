package sigma.semantics.expressions

import sigma.semantics.BuiltinTypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.SetType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.SetConstructorTerm
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals

object SetConstructorTests {
    object TypeCheckingTests {
        @Test
        fun testSingleElement() {
            val setConstructor = SetConstructor.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "value1" to BoolType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = "{value1}",
                ) as SetConstructorTerm,
            )

            assertEquals(
                expected = SetType(
                    elementType = BoolType,
                ),
                actual = setConstructor.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesCompatibleElements() {
            val setConstructor = SetConstructor.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "value1" to BoolType,
                        "value2" to BoolType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            value1,
                            value2,
                        }
                    """.trimIndent(),
                ) as SetConstructorTerm,
            )

            assertEquals(
                expected = SetType(
                    elementType = BoolType,
                ),
                actual = setConstructor.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleElements() {
            val setConstructor = SetConstructor.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "value1" to BoolType,
                        "value2" to IntCollectiveType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            value1,
                            value2,
                        }
                    """.trimIndent(),
                ) as SetConstructorTerm,
            )

            assertEquals(
                expected = setOf(
                    SetConstructor.InconsistentElementTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = setConstructor.errors,
            )

            assertEquals(
                expected = IllType,
                actual = setConstructor.inferredType.value,
            )
        }
    }
}

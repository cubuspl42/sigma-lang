package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.SetValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.ValueResult
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.SetType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.SetConstructorTerm
import utils.FakeStaticBlock
import utils.FakeValueDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

object SetConstructorTests {
    object TypeCheckingTests {
        @Test
        fun testSingleElement() {
            val setConstructor = SetConstructor.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value2"),
                        type = IntCollectiveType,
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

    object EvaluationTests {
        @Test
        fun testSimple() {
            val setConstructor = SetConstructor.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse("{foo, bar, baz}") as SetConstructorTerm,
            )

            val result = assertIs<ValueResult>(
                setConstructor.bind(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("foo") to IntValue(value = 1L),
                            Symbol.of("bar") to IntValue(value = 2L),
                            Symbol.of("baz") to IntValue(value = 3L),
                        ),
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = SetValue(
                    elements = setOf(
                        IntValue(value = 1L),
                        IntValue(value = 2L),
                        IntValue(value = 3L),
                    )
                ),
                actual = result.value,
            )
        }
    }
}

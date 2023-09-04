package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorSourceTerm
import utils.FakeStaticBlock
import utils.FakeDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SetConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testSingleElement() {
            val setConstructor = SetConstructor.build(
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = "{value1}",
                ) as SetConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            value1,
                            value2,
                        }
                    """.trimIndent(),
                ) as SetConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value2"),
                        type = IntCollectiveType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            value1,
                            value2,
                        }
                    """.trimIndent(),
                ) as SetConstructorSourceTerm,
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

    class EvaluationTests {
        @Test
        fun testSimple() {
            val setConstructor = SetConstructor.build(
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse("{foo, bar, baz}") as SetConstructorSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                setConstructor.bind(
                    dynamicScope = FixedDynamicScope(
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

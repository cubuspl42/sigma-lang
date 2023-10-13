package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorSourceTerm
import utils.FakeDefinition
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SetConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testSingleElement() {
            val setConstructor = SetConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = "{value1}",
                ) as SetConstructorSourceTerm,
            ).resolved

            assertEquals(
                expected = SetType(
                    elementType = BoolType,
                ),
                actual = setConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesCompatibleElements() {
            val setConstructor = SetConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = BoolType,
                        ),
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
            ).resolved

            assertEquals(
                expected = SetType(
                    elementType = BoolType,
                ),
                actual = setConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleElements() {
            val setConstructor = SetConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = IntCollectiveType,
                        ),
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
            ).resolved

            assertEquals(
                expected = setOf(
                    SetConstructor.InconsistentElementTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = setConstructor.directErrors,
            )

            assertEquals(
                expected = IllType,
                actual = setConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testSimple() {
            val term = ExpressionSourceTerm.parse("{foo, bar, baz}") as SetConstructorSourceTerm

            val setConstructor = SetConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("foo"),
                            type = NeverType,
                            value = IntValue(value = 1L),
                        ),
                        FakeDefinition(
                            name = Identifier.of("bar"),
                            type = NeverType,
                            value = IntValue(value = 2L),
                        ),
                        FakeDefinition(
                            name = Identifier.of("baz"),
                            type = NeverType,
                            value = IntValue(value = 3L),
                        ),
                    ),
                ),
                term = term,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                setConstructor.bind(
                    dynamicScope = DynamicScope.Empty,
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

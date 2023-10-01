package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationStackExhaustionError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LetExpressionTests {
    class TypeCheckingTests {
        @Test
        fun testValidRecursiveDefinitions() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        f = ^[] -> Int => g[],
                        g = ^[] => f[],
                    } %in f[]
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val let = LetExpression.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("f"),
                ),
            )

            val fType = assertIs<FunctionType>(
                value = fDefinition.computedEffectiveType.getOrCompute(),
            )

            assertIs<IntType>(value = fType.imageType)

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("g"),
                ),
            )

            val gType = assertIs<FunctionType>(
                value = gDefinition.computedEffectiveType.getOrCompute(),
            )

            assertIs<IntType>(value = gType.imageType)
        }

        @Test
        fun testCyclicRecursiveTypeInference() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        f = ^[] => g[],
                        g = ^[] => f[],
                    } %in f[]
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val letExpression = LetExpression.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Symbol.of("f"),
                ),
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType.Empty,
                    imageType = IllType,
                ),
                actual = fDefinition.computedEffectiveType.getOrCompute(),
            )

            val gDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Symbol.of("g"),
                ),
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType.Empty,
                    imageType = IllType,
                ),
                actual = gDefinition.computedEffectiveType.getOrCompute(),
            )
        }

        @Test
        fun testCyclicRecursiveDefinitions() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        a: Int = b,
                        b: Int = a,
                    } %in f[]
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val let = LetExpression.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val aDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("a"),
                ),
            )

            assertIs<IntCollectiveType>(
                value = aDefinition.computedEffectiveType.getOrCompute(),
            )

            val bDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("b"),
                ),
            )

            assertIs<IntCollectiveType>(
                value = bDefinition.computedEffectiveType.getOrCompute(),
            )
        }

        @Test
        fun testReferringConst() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Test (
                        %const a = 42
                        
                        %const b = %let {
                            c = a,
                        } %in c
                    )
                """.trimIndent(),
            )

            val namespaceDefinition = NamespaceDefinition.build(
                outerScope = StaticScope.Empty,
                qualifiedPath = QualifiedPath.Root,
                term = term,
            )

            val bDefinition = namespaceDefinition.getDefinition(name = Symbol.of("b")) as UserConstantDefinition

            val letExpression = bDefinition.assignedBody as LetExpression

            val classifiedValue = assertIs<ConstClassificationContext<Value>>(
                letExpression.classifiedValue
            )

            assertEquals(
                expected = IntValue(value = 42L),
                actual = classifiedValue.valueThunk.value,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testCyclicRecursiveDefinitions() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        a: Int = b,
                        b: Int = a,
                    } %in a
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val let = LetExpression.build(
                outerScope = BuiltinScope,
                term = term,
            )

            assertIs<EvaluationStackExhaustionError>(
                let.bind(dynamicScope = DynamicScope.Empty).outcome,
            )
        }
    }
}

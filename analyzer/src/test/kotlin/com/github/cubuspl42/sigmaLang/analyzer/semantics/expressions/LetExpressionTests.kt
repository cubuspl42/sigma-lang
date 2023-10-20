@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationStackExhaustionError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LetExpressionTests {
    class TypeCheckingTests {
        @Test
        @Ignore // TODO: Directly cyclic references
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
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val aDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Identifier.of("a"),
                ),
            )

            assertIs<IntCollectiveType>(
                value = aDefinition.computedBodyType.getOrCompute(),
            )

            val bDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Identifier.of("b"),
                ),
            )

            assertIs<IntCollectiveType>(
                value = bDefinition.computedBodyType.getOrCompute(),
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
                context = Expression.BuildContext.Empty,
                qualifiedPath = QualifiedPath.Root,
                term = term,
            )

            val bDefinition = namespaceDefinition.getDefinition(name = Identifier.of("b")) as Definition

            val letExpression = bDefinition.body

            val classifiedValue = assertIs<ConstExpression>(
                letExpression.classified
            )

            assertEquals(
                expected = IntValue(value = 42L),
                actual = classifiedValue.valueThunk.value,
            )
        }
    }

    class EvaluationTests {
        @Test
        @Ignore // TODO: Directly cyclic references
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
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            assertIs<EvaluationStackExhaustionError>(
                let.resultStub.resolved.bind(dynamicScope = DynamicScope.Empty).outcome,
            )
        }
    }
}

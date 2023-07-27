package sigma.semantics.expressions

import sigma.semantics.BuiltinScope
import sigma.semantics.Computation
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IntType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.LetExpressionTerm
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LetExpressionTests {
    object TypeCheckingTests {
        @Test
        fun testValidRecursiveDefinitions() {
            val term = ExpressionTerm.parse(
                source = """
                    let {
                        f = ^[] -> Int => g[],
                        g =^ [] => f[],
                    } in f[]
                """.trimIndent(),
            ) as LetExpressionTerm

            val let = LetExpression.build(
                outerDeclarationScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("f"),
                ),
            )

            val fType = assertIs<FunctionType>(
                value = fDefinition.effectiveValueType.value,
            )

            assertIs<IntType>(value = fType.imageType)

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("g"),
                ),
            )

            val gType = assertIs<FunctionType>(
                value = gDefinition.effectiveValueType.value,
            )

            assertIs<IntType>(value = gType.imageType)
        }

        @Test
        fun testCyclicRecursiveDefinitions() {
            val term = ExpressionTerm.parse(
                source = """
                    let {
                        f = ^[] => g[],
                        g = ^[] => f[],
                    } in f[]
                """.trimIndent(),
            ) as LetExpressionTerm

            val let = LetExpression.build(
                outerDeclarationScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("f"),
                ),
            )

            assertIs<Computation.Result.CyclicError<*>>(
                value = fDefinition.effectiveValueType.result,
            )

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Symbol.of("g"),
                ),
            )

            assertIs<Computation.Result.CyclicError<*>>(
                value = gDefinition.effectiveValueType.result,
            )
        }
    }
}

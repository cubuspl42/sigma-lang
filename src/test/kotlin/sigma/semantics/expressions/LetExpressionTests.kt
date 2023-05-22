package sigma.semantics.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.Computation
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
                        f = [] -> Int => g[],
                        g = [] => f[],
                    } in f[]
                """.trimIndent(),
            ) as LetExpressionTerm

            val let = LetExpression.build(
                typeScope = BuiltinTypeScope,
                outerDeclarationScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getDefinition(
                    name = Symbol.of("f"),
                ),
            )

            val fType = assertIs<FunctionType>(
                value = fDefinition.inferredValueType.value,
            )

            assertIs<IntType>(value = fType.imageType)

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getDefinition(
                    name = Symbol.of("g"),
                ),
            )

            val gType = assertIs<FunctionType>(
                value = gDefinition.inferredValueType.value,
            )

            assertIs<IntType>(value = gType.imageType)
        }

        @Test
        fun testCyclicRecursiveDefinitions() {
            val term = ExpressionTerm.parse(
                source = """
                    let {
                        f = [] => g[],
                        g = [] => f[],
                    } in f[]
                """.trimIndent(),
            ) as LetExpressionTerm

            val let = LetExpression.build(
                typeScope = BuiltinTypeScope,
                outerDeclarationScope = BuiltinScope,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getDefinition(
                    name = Symbol.of("f"),
                ),
            )

            assertIs<Computation.Result.CyclicError<*>>(
                value = fDefinition.inferredValueType.result,
            )

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getDefinition(
                    name = Symbol.of("g"),
                ),
            )

            assertIs<Computation.Result.CyclicError<*>>(
                value = gDefinition.inferredValueType.result,
            )
        }
    }
}

package sigma.semantics.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IntType
import sigma.semantics.types.TupleType
import sigma.semantics.types.TypeVariable
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.ExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AbstractionTests {
    object TypeCheckingTests {
        object InferredTypeTests {
            @Test
            fun testInferredFromValue() {
                val term = ExpressionTerm.parse(
                    source = "{(a: Int)} => 2 + 3",
                ) as AbstractionTerm

                val abstraction = Abstraction.build(
                    outerTypeScope = BuiltinTypeScope,
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<IntType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testInferredFromDeclaration() {
                val term = ExpressionTerm.parse(
                    source = "{(a: Int)} -> Bool => 3 + 4",
                ) as AbstractionTerm

                val abstraction = Abstraction.build(
                    outerTypeScope = BuiltinTypeScope,
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<BoolType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testInferredFromArguments() {
                val term = ExpressionTerm.parse(
                    source = "{(a: Int)} => a",
                ) as AbstractionTerm

                val abstraction = Abstraction.build(
                    outerTypeScope = BuiltinTypeScope,
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<IntType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testDeclaredFromGenericArguments() {
                val term = ExpressionTerm.parse(
                    source = "![e] {(a: e)} -> e => a",
                ) as AbstractionTerm

                val abstraction = Abstraction.build(
                    outerTypeScope = BuiltinTypeScope,
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertEquals(
                    expected = inferredType.argumentType,
                    actual = TupleType.ordered(
                        TupleType.OrderedEntry(
                            index = 0,
                            name = Symbol.of("a"),
                            type = TypeVariable,
                        ),
                    ),
                )

                assertIs<TypeVariable>(
                    value = inferredType.imageType,
                )
            }
        }
    }
}

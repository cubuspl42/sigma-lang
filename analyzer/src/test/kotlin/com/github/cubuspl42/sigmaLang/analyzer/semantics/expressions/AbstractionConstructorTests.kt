@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableAbstraction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class AbstractionConstructorTests {
    class BuildingTests {
        @Test
        fun testDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                source = "^[a: Int] -> Int => a + 3",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = abstractionConstructor.declaredImageType,
            )
        }
    }

    class TypeInferenceTests {
        @Test
        fun testWithDeclaredImageType_fromBuiltIn() {
            val term = ExpressionSourceTerm.parse(
                // The declared image type is a built-in type
                source = "^[a: Int] -> Bool => 3 + 4",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val inferredType = assertIs<FunctionType>(
                value = abstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )

            assertIs<BoolType>(
                value = inferredType.imageType,
            )
        }

        @Test
        fun testWithDeclaredImageType_fromMetaArgument() {
            val term = ExpressionSourceTerm.parse(
                // The declared image type is an introduced meta-argument
                source = "!^[e: Type] ^[a: e] -> e => a",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val inferredType = assertIs<FunctionType>(
                value = abstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("a"),
                            type = TypeVariable(
                                formula = Formula.of("e"),
                            ),
                        ),
                    ),
                ),
                actual = inferredType.argumentType,
            )

            assertIs<TypeVariable>(
                value = inferredType.imageType,
            )
        }


        @Test
        @Ignore // TODO: Const analysis of arbitrary expression
        fun testWithDeclaredImageType_fromMetaArgumentComplex() {
            val term = ExpressionSourceTerm.parse(
                // The declared image type is a complex expression based on a meta-argument
                source = """
                        !^[
                            t: ^{t1: Type, t2: Type},
                        ] ^[
                            l: ^[t.t1...],
                            f: ^[t.t1] -> t.t2,
                        ] -> ^[t.t2...] => map[l, f]
                    """.trimIndent(),
            ) as AbstractionConstructorSourceTerm

            val abstraction = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                abstraction.errors,
            )

            val inferredType = assertIs<FunctionType>(
                value = abstraction.inferredTypeOrIllType.getOrCompute(),
            )

            val innerAbstractionType = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("l"),
                            type = ArrayType(
                                elementType = TypeVariable.of("t.t1"), // FIXME
                            ),
                        ),
                        OrderedTupleType.Element(
                            name = Symbol.of("f"),
                            type = UniversalFunctionType(
                                argumentType = OrderedTupleType.of(
                                    TypeVariable.of("t.t1"), // FIXME
                                ),
                                imageType = TypeVariable.of("t.t2"), // FIXME
                            ),
                        ),
                    ),
                ),
                imageType = ArrayType(
                    elementType = TypeVariable.of("t.t2"), // FIXME
                ),
            )

            val metaAbstractionType = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("t"),
                            type = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    Symbol.of("t1") to TypeType,
                                    Symbol.of("t2") to TypeType,
                                ),
                            ),
                        ),
                    ),
                ),
                imageType = innerAbstractionType,
            )

            assertEquals(
                expected = metaAbstractionType,
                actual = inferredType,
            )
        }

        @Test
        fun testWithoutDeclaredImageType_fromBodyOnly() {
            val term = ExpressionSourceTerm.parse(
                // Image type has to be inferred from the body only
                source = "^[a: Int] => 2 + 3",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val inferredType = assertIs<FunctionType>(
                value = abstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )

            assertIs<IntType>(
                value = inferredType.imageType,
            )
        }

        @Test
        fun testWithoutDeclaredImageType_fromArgument() {
            val term = ExpressionSourceTerm.parse(
                // Image type has to be inferred based on the argument type
                source = "^[a: Int] => a",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val inferredType = assertIs<FunctionType>(
                value = abstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )

            assertIs<IntType>(
                value = inferredType.imageType,
            )
        }

        @Test
        fun testSelfRecursive_withDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        f = ^[n: Int] -> Bool => f[n + 1]
                    } %in f
                """.trimIndent(),
            )

            val expression = Expression.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val type = expression.inferredTypeOrIllType.getOrCompute()

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("n"),
                                type = IntCollectiveType,
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }

        @Test
        fun testMutuallyRecursive_oneWithDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                // Two mutually recursive abstractions, one of them has a declared image type
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
        fun testMutuallyRecursive_withoutDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                // Two mutually recursive abstractions, none of them has a declared image type
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
    }

    class ClassificationTests {
        @Test
        fun testBound() {
            val term = ExpressionSourceTerm.parse(
                source = "^[a: Int] => a * 2",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope,
                term = term,
            )

            val classifiedValue = abstractionConstructor.classifiedValue

            val constValue = assertIs<ConstClassificationContext<Value>>(
                classifiedValue,
            )

            assertIs<ComputableAbstraction>(
                constValue.valueThunk.value
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstractionConstructor = AbstractionConstructor.build(
                outerScope = BuiltinScope, term = ExpressionSourceTerm.parse(
                    source = "^[n: Int, m: Int] => n * m",
                ) as AbstractionConstructorSourceTerm
            )

            val result = assertIs<EvaluationResult<Value>>(
                abstractionConstructor.bind(
                    dynamicScope = BuiltinScope,
                ).evaluateInitial(),
            )

            val closure = result.value

            assertIs<FunctionValue>(closure)

            val callResult = assertIs<EvaluationResult<Value>>(
                closure.apply(
                    argument = ArrayTable(
                        elements = listOf(
                            IntValue(2),
                            IntValue(3),
                        ),
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(6),
                actual = callResult.value,
            )
        }
    }
}

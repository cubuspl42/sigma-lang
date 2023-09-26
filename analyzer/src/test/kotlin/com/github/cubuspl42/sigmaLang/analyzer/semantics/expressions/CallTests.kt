package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CallTests {
    class TypeCheckingTests {
        @Test
        fun testLegalSubject() {
            val term = ExpressionSourceTerm.parse(
                source = "f[false]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("f"),
                        type = UniversalFunctionType(
                            argumentType = OrderedTupleType(
                                elements = listOf(
                                    OrderedTupleType.Element(
                                        name = null,
                                        type = BoolType,
                                    ),
                                ),
                            ),
                            imageType = IntCollectiveType,
                        ),
                    ),
                ).chainWith(
                    outerScope = BuiltinScope,
                ),
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertIs<IntType>(
                value = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testIllegalOrderedArgument() {
            val term = ExpressionSourceTerm.parse(
                source = "f[1]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("f"),
                        type = UniversalFunctionType(
                            argumentType = OrderedTupleType(
                                elements = listOf(
                                    OrderedTupleType.Element(
                                        name = Symbol.of("a"),
                                        type = BoolType,
                                    ),
                                ),
                            ),
                            imageType = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = setOf(
                    Call.InvalidArgumentError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        matchResult = OrderedTupleType.OrderedTupleMatch(
                            elementsMatches = listOf(
                                MembershipType.TotalMismatch(
                                    expectedType = BoolType,
                                    actualType = IntLiteralType(
                                        value = IntValue(value = 1L),
                                    ),
                                )
                            ),
                            sizeMatch = OrderedTupleType.OrderedTupleMatch.SizeMatch,
                        ),
                    ),
                ),
                actual = call.directErrors,
            )

            // Wrong argument does not affect the inferred type
            assertIs<IntType>(
                value = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testIllegalSubject() {
            val term = ExpressionSourceTerm.parse(
                source = "b[1]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("b"),
                        type = BoolType,
                    ),
                ),
                term = term,
            )

            assertIs<IllType>(
                value = call.inferredTypeOrIllType.getOrCompute(),
            )

            assertEquals(
                expected = setOf(
                    Call.NonFunctionCallError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        illegalSubjectType = BoolType,
                    ),
                ),
                actual = call.directErrors,
            )
        }

        @Test
        fun testInferableGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[false, 0]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("f"),
                        type = UniversalFunctionType(
                            genericParameters = setOf(
                                TypeVariable.of("type1"),
                                TypeVariable.of("type2"),
                            ),
                            argumentType = OrderedTupleType.of(
                                TypeVariable.of("type1"),
                                TypeVariable.of("type2"),
                            ),
                            imageType = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    Symbol.of("key1") to TypeVariable.of("type1"),
                                    Symbol.of("key2") to TypeVariable.of("type2"),
                                ),
                            ),
                        ),
                    ),
                ).chainWith(BuiltinScope),
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key1") to BoolType,
                        Symbol.of("key2") to IntLiteralType.of(0L),
                    ),
                ),
                actual = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testNonInferableGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("f"),
                        type = UniversalFunctionType(
                            genericParameters = setOf(
                                TypeVariable.of("type"),
                            ),
                            argumentType = OrderedTupleType.Empty,
                            imageType = ArrayType(
                                TypeVariable.of("type"),
                            ),
                        ),
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = setOf(
                    Call.NonFullyInferredCalleeTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        calleeGenericType = UniversalFunctionType(
                            genericParameters = setOf(
                                TypeVariable.of("type"),
                            ),
                            argumentType = OrderedTupleType.Empty,
                            imageType = ArrayType(
                                TypeVariable.of("type"),
                            ),
                        ),
                        nonInferredTypeVariables = setOf(
                            TypeVariable.of("type"),
                        ),
                    )
                ),
                actual = call.directErrors,
            )

            assertEquals(
                expected = IllType,
                actual = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testNestedGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[false, 1]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("f"),
                        type = UniversalFunctionType(
                            genericParameters = setOf(
                                TypeVariable.of("type1"),
                                TypeVariable.of("type2"),
                            ),
                            argumentType = OrderedTupleType.of(
                                TypeVariable.of("type1"),
                                TypeVariable.of("type2"),
                            ),
                            imageType = UniversalFunctionType(
                                genericParameters = setOf(
                                    TypeVariable.of("type2"),
                                ),
                                argumentType = OrderedTupleType.of(
                                    TypeVariable.of("type1"),
                                    TypeVariable.of("type2"),
                                ),
                                imageType = OrderedTupleType.of(
                                    TypeVariable.of("type1"),
                                    TypeVariable.of("type2"),
                                ),
                            ),
                        ),
                    ),
                ).chainWith(BuiltinScope),
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    genericParameters = setOf(
                        TypeVariable.of("type2"),
                    ),
                    argumentType = OrderedTupleType.of(
                        BoolType,
                        TypeVariable.of("type2"),
                    ),
                    imageType = OrderedTupleType.of(
                        BoolType,
                        TypeVariable.of("type2"),
                    ),
                ),
                actual = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testExcessiveOrderedArguments() {
            // TODO
        }

        @Test
        fun testExcessiveUnorderedArguments() {
            // TODO
        }
    }

    class EvaluationTests {
        @Test
        fun testSimple() {
            val sq = object : ComputableFunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val n = argument as IntValue
                    return IntValue(n.value * n.value).toThunk()
                }
            }

            val call = Call.build(
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse("sq(3)") as PostfixCallSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    dynamicScope = FixedDynamicScope(
                        entries = mapOf(
                            Symbol.of("sq") to sq,
                        )
                    ),
                ).evaluateInitial(),
            )
            assertEquals(
                expected = IntValue(9),
                actual = result.value,
            )
        }

        @Test
        fun testDictSubject() {
            val call = Call.build(
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse("dict(2)") as PostfixCallSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    dynamicScope = FixedDynamicScope(
                        entries = mapOf(
                            Symbol.of("dict") to DictValue(
                                entries = mapOf(
                                    IntValue(1) to Symbol.of("one"),
                                    IntValue(2) to Symbol.of("two"),
                                    IntValue(3) to Symbol.of("three"),
                                ),
                            ),
                        ),
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = Symbol.of("two"),
                actual = result.value,
            )
        }
    }
}

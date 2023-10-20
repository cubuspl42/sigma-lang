package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call.NonFullyInferredCalleeTypeError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.CollectionMatchers
import utils.FakeDefinition
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import utils.Matcher
import utils.assertMatches
import utils.assertTypeIsEquivalent
import utils.checked
import utils.whichHasSize
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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            annotatedType = UniversalFunctionType(
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
                ),
                term = term,
            ).resolved

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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            annotatedType = UniversalFunctionType(
                                argumentType = OrderedTupleType(
                                    elements = listOf(
                                        OrderedTupleType.Element(
                                            name = Identifier.of("a"),
                                            type = BoolType,
                                        ),
                                    ),
                                ),
                                imageType = IntCollectiveType,
                            ),
                        ),
                    ),
                ),
                term = term,
            ).resolved

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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("b"),
                            annotatedType = BoolType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

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

            val typeVariableDefinition1 = TypeVariableDefinition(
                name = Identifier.of("type1"),
            )

            val typeVariableDefinition2 = TypeVariableDefinition(
                name = Identifier.of("type2"),
            )

            val call = Call.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            annotatedType = UniversalFunctionType(
                                argumentType = OrderedTupleType.of(
                                    typeVariableDefinition1.typePlaceholder,
                                    typeVariableDefinition2.typePlaceholder,
                                ),
                                imageType = UnorderedTupleType(
                                    valueTypeByName = mapOf(
                                        Identifier.of("key1") to typeVariableDefinition1.typePlaceholder,
                                        Identifier.of("key2") to typeVariableDefinition2.typePlaceholder,
                                    ),
                                ),
                            ),
                        ),
                    ).chainWith(BuiltinScope),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key1") to BoolType,
                        Identifier.of("key2") to IntLiteralType.of(0L),
                    ),
                ),
                actual = call.inferredTypeOrIllType.getOrCompute() as MembershipType,
            )
        }

        @Test
        fun testNonInferableGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[]",
            ) as PostfixCallSourceTerm

            val typeVariableDefinition = TypeVariableDefinition(
                name = Identifier.of("type"),
            )

            val call = Call.build(
                Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            annotatedType = UniversalFunctionType(
                                argumentType = OrderedTupleType.Empty,
                                imageType = ArrayType(
                                    typeVariableDefinition.typePlaceholder,
                                ),
                            ),
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = setOf(
                    NonFullyInferredCalleeTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        calleeGenericType = UniversalFunctionType(
                            argumentType = OrderedTupleType.Empty,
                            imageType = ArrayType(
                                typeVariableDefinition.typePlaceholder,
                            ),
                        ),
                        unresolvedPlaceholders = setOf(
                            typeVariableDefinition.typePlaceholder,
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
        fun testNonInferableGenericCall_nested() {
            val term = ExpressionSourceTerm.parse(
                source = "f[false, 1]",
            ) as PostfixCallSourceTerm

            val innerTypeDefinition1 = TypeVariableDefinition(
                name = Identifier.of("type1"),
            )

            val innerTypeDefinition2 = TypeVariableDefinition(
                name = Identifier.of("type2"),
            )

            val innerFunctionType = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    innerTypeDefinition1.typePlaceholder,
                    innerTypeDefinition2.typePlaceholder,
                ),
                imageType = OrderedTupleType.of(
                    innerTypeDefinition1.typePlaceholder,
                    innerTypeDefinition2.typePlaceholder,
                ),
            )

            val outerTypeDefinition1 = TypeVariableDefinition(
                name = Identifier.of("type1"),
            )

            val outerTypeDefinition2 = TypeVariableDefinition(
                name = Identifier.of("type2"),
            )

            val outerFunctionType = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    outerTypeDefinition1.typePlaceholder,
                    outerTypeDefinition2.typePlaceholder,
                ),
                imageType = innerFunctionType,
            )

            val call = Call.build(
                Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            annotatedType = outerFunctionType,
                        ),
                    ).chainWith(BuiltinScope),
                ),

                term = term,
            ).resolved

            assertMatches(
                matcher = CollectionMatchers.eachOnce(
                    elements = setOf(
                        CallMatchers.NonFullyInferredCalleeTypeErrorMatcher(
                            calleeGenericType = Matcher.Is<UniversalFunctionType>(),
                            unresolvedPlaceholders = CollectionMatchers.whereEvery(
                                element = Matcher.Is<TypePlaceholder>(),
                            ).whichHasSize(
                                expectedSize = 2,
                            )
                        ).checked(),
                    ),
                ),
                actual = call.directErrors,
            )

            assertMatches(
                matcher = Matcher.Equals(IllType),
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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("sq"),
                            type = NeverType,
                            value = sq,
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse("sq(3)") as PostfixCallSourceTerm,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    dynamicScope = DynamicScope.Empty,
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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("dict"),
                            type = NeverType,
                            value = DictValue.fromMap(
                                entries = mapOf(
                                    IntValue(1) to StringValue.of("one"),
                                    IntValue(2) to StringValue.of("two"),
                                    IntValue(3) to StringValue.of("three"),
                                ),
                            ),
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse("dict(2)") as PostfixCallSourceTerm,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    dynamicScope = DynamicScope.Empty
                ).evaluateInitial(),
            )

            assertEquals(
                expected = StringValue.of("two"),
                actual = result.value,
            )
        }

        @Test
        fun testStrictness() {
            val term = ExpressionSourceTerm.parse("f{a: 42, b: 1 / 0}") as PostfixCallSourceTerm

            val fValue = object : ComputableFunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val arguments = argument as DictValue
                    val aValue = arguments.read(Identifier.of("a"))
                    return aValue!!
                }
            }

            val call = Call.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("f"),
                            type = NeverType,
                            value = fValue,
                        ),
                    ).chainWith(
                        outerScope = BuiltinScope, // For arithmetic division
                    ),
                ),
                term = term,
            ).resolved


            val resultThunk = call.bind(
                dynamicScope = DynamicScope.Empty,
            )

            assertEquals(
                expected = IntValue(value = 42L),
                actual = resultThunk.value,
            )
        }
    }
}

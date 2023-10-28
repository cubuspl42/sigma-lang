@file:Suppress("JUnitMalformedDeclaration")

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
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ParametricType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.CollectionMatchers
import utils.FakeArgumentDeclarationBlock
import utils.FakeDefinition
import utils.FakeDefinitionBlock
import utils.FakeStaticScope
import utils.FakeUserDeclaration
import utils.Matcher
import utils.assertMatches
import utils.checked
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
                    outerScope = FakeArgumentDeclarationBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            declaredType = UniversalFunctionType(
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            declaredType = UniversalFunctionType(
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
                                SpecificType.TotalMismatch(
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("b"),
                            declaredType = BoolType,
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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeArgumentDeclarationBlock.of(
                        FakeUserDeclaration(name = Identifier.of("f"), declaredType = run {
                            val parameterDeclaration = AbstractionConstructor.ArgumentDeclaration(
                                declaredType = UnorderedTupleType(
                                    valueTypeByName = mapOf(
                                        Identifier.of("x") to TypeType,
                                        Identifier.of("y") to TypeType,
                                    ),
                                ),
                            )

                            val xTypeVariable = TypeVariable(
                                traitDeclaration = parameterDeclaration,
                                path = TypeVariable.Path.Root.extend(Identifier.of("x")),
                            )

                            val yTypeVariable = TypeVariable(
                                traitDeclaration = parameterDeclaration,
                                path = TypeVariable.Path.Root.extend(Identifier.of("y")),
                            )

                            GenericType(
                                parameterDeclaration = parameterDeclaration,
                                bodyType = UniversalFunctionType(
                                    argumentType = OrderedTupleType.of(
                                        xTypeVariable,
                                        yTypeVariable,
                                    ),
                                    imageType = UnorderedTupleType(
                                        valueTypeByName = mapOf(
                                            Identifier.of("key1") to xTypeVariable,
                                            Identifier.of("key2") to yTypeVariable,
                                        ),
                                    ),
                                ),
                            )
                        }),
                    ).chainWith(BuiltinScope),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertMatches(
                matcher = UnorderedTupleTypeMatcher(
                    entries = CollectionMatchers.eachOnce(
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(Identifier.of("key1")),
                            type = Matcher.Is<BoolType>(),
                        ),
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(Identifier.of("key2")),
                            type = Matcher.Is<IntType>(),
                        ),
                    )
                ).checked(),
                actual = call.inferredTypeOrIllType.getOrCompute() as SpecificType,
            )
        }

        @Test
        fun testNonInferableGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[]",
            ) as PostfixCallSourceTerm

            val parameterDeclaration = AbstractionConstructor.ArgumentDeclaration(
                declaredType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("x") to TypeType,
                    ),
                ),
            )

            val xTypeVariable = TypeVariable(
                traitDeclaration = parameterDeclaration,
                path = TypeVariable.Path.Root.extend(Identifier.of("x")),
            )

            val fType = GenericType(
                parameterDeclaration = parameterDeclaration,
                bodyType = UniversalFunctionType(
                    argumentType = OrderedTupleType.Empty,
                    imageType = xTypeVariable,
                ),
            )

            val call = Call.build(
                Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            declaredType = fType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertMatches(
                matcher = CollectionMatchers.eachOnce(
                    CallMatcher.NonFullyInferredCalleeTypeErrorMatcher(
                        calleeGenericType = Matcher.Is<FunctionType>(),
                        unresolvedPlaceholders = CollectionMatchers.eachOnce(
                            Matcher.Equals(TypePlaceholder(typeVariable = xTypeVariable))
                        )
                    ).checked(),
                ),
                actual = call.directErrors,
            )

            assertEquals(
                expected = IllType,
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
                    outerScope = FakeStaticScope.of(
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
                    outerScope = FakeStaticScope.of(
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
                    outerScope = FakeDefinitionBlock.of(
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

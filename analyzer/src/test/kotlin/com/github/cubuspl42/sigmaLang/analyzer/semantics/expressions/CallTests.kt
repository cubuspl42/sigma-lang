package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
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
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.FakeDefinition
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import utils.assertTypeIsEquivalent
import java.lang.ArithmeticException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
                            type = UniversalFunctionType(
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
                            type = BoolType,
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

            val call = Call.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            type = UniversalFunctionType(
                                metaArgumentType = OrderedTupleType(
                                    elements = listOf(
                                        OrderedTupleType.Element(
                                            name = Identifier.of("type1"),
                                            type = TypeType,
                                        ),
                                        OrderedTupleType.Element(
                                            name = Identifier.of("type2"),
                                            type = TypeType,
                                        ),
                                    )
                                ),
                                argumentType = OrderedTupleType.of(
                                    TypeVariable.of("type1"),
                                    TypeVariable.of("type2"),
                                ),
                                imageType = UnorderedTupleType(
                                    valueTypeByName = mapOf(
                                        Identifier.of("key1") to TypeVariable.of("type1"),
                                        Identifier.of("key2") to TypeVariable.of("type2"),
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
                actual = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testNonInferableGenericCall() {
            val term = ExpressionSourceTerm.parse(
                source = "f[]",
            ) as PostfixCallSourceTerm

            val call = Call.build(
                Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            type = UniversalFunctionType(
                                metaArgumentType = OrderedTupleType(
                                    elements = listOf(
                                        OrderedTupleType.Element(
                                            name = Identifier.of("type"),
                                            type = TypeType,
                                        ),
                                    )
                                ),
                                argumentType = OrderedTupleType.Empty,
                                imageType = ArrayType(
                                    TypeVariable.of("type"),
                                ),
                            ),
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = setOf(
                    Call.NonFullyInferredCalleeTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        calleeGenericType = UniversalFunctionType(
                            metaArgumentType = OrderedTupleType(
                                elements = listOf(
                                    OrderedTupleType.Element(
                                        name = Identifier.of("type"),
                                        type = TypeType,
                                    ),
                                )
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
                Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("f"),
                            type = UniversalFunctionType(
                                metaArgumentType = OrderedTupleType(
                                    elements = listOf(
                                        OrderedTupleType.Element(
                                            name = Identifier.of("type1"),
                                            type = TypeType,
                                        ),
                                        OrderedTupleType.Element(
                                            name = Identifier.of("type2"),
                                            type = TypeType,
                                        ),
                                    )
                                ),
                                argumentType = OrderedTupleType.of(
                                    TypeVariable.of("type1"),
                                    TypeVariable.of("type2"),
                                ),
                                imageType = UniversalFunctionType(
                                    metaArgumentType = OrderedTupleType(
                                        elements = listOf(
                                            OrderedTupleType.Element(
                                                name = Identifier.of("type2"),
                                                type = TypeType,
                                            ),
                                        )
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
                ),

                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    metaArgumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Identifier.of("type2"),
                                type = TypeType,
                            ),
                        )
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

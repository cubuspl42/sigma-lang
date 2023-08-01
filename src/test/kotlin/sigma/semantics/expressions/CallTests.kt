package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.ComputableFunctionValue
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.asThunk
import sigma.semantics.BuiltinScope
import sigma.semantics.StaticScope
import sigma.semantics.types.ArrayType
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.IntType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.ExpressionTerm
import utils.FakeStaticBlock
import utils.FakeValueDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CallTests {
    class TypeCheckingTests {
        @Test
        fun testLegalSubject() {
            val term = ExpressionTerm.parse(
                source = "f[false]",
            ) as CallTerm

            val call = Call.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
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
                actual = call.errors,
            )

            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalOrderedArgument() {
            val term = ExpressionTerm.parse(
                source = "f[1]",
            ) as CallTerm

            val call = Call.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
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
                                Type.TotalMismatch(
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
                actual = call.errors,
            )

            // Wrong argument does not affect the inferred type
            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalSubject() {
            val term = ExpressionTerm.parse(
                source = "b[1]",
            ) as CallTerm

            val call = Call.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("b"),
                        type = BoolType,
                    ),
                ),
                term = term,
            )

            assertIs<IllType>(
                value = call.inferredType.value,
            )

            assertEquals(
                expected = setOf(
                    Call.NonFunctionCallError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        illegalSubjectType = BoolType,
                    ),
                ),
                actual = call.errors,
            )
        }

        @Test
        fun testInferableGenericCall() {
            val term = ExpressionTerm.parse(
                source = "f[false, 0]",
            ) as CallTerm

            val call = Call.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
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
                actual = call.errors,
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key1") to BoolType,
                        Symbol.of("key2") to IntLiteralType.of(0L),
                    ),
                ),
                actual = call.inferredType.value,
            )
        }

        @Test
        fun testNonInferableGenericCall() {
            val term = ExpressionTerm.parse(
                source = "f[]",
            ) as CallTerm

            val call = Call.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
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
                actual = call.errors,
            )

            assertEquals(
                expected = IllType,
                actual = call.inferredType.value,
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
                    return IntValue(n.value * n.value).asThunk
                }
            }

            val call = Call.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse("sq(3)") as CallTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    scope = FixedScope(
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
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse("dict(2)") as CallTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                call.bind(
                    scope = FixedScope(
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

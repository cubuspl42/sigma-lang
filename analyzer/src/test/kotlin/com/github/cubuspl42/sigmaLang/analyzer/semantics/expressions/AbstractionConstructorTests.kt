@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableAbstraction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedUnorderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.semantics.buildReferenceMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.Builtin
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import utils.CollectionMatchers
import utils.FakeStaticScope
import utils.FakeUserDeclaration
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@Suppress("unused")
class AbstractionConstructorTests {
    class ConstructionTests {
        @Test
        fun testDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                source = "^[a: Int] -> Int => a + 3",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).expression

            val argumentDeclaration = abstractionConstructor.argumentDeclaration

            assertMatches(
                matcher = AbstractionConstructorMatcher(
                    argumentType = OrderedTupleTypeMatcher(
                        elements = listOf(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("a")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                        ),
                    ).checked(),
                    declaredImageType = Matcher.Is<IntCollectiveType>(),
                    image = CallMatcher(
                        subject = Matcher.Is<Builtin>(),
                        argument = UnorderedTupleConstructorMatcher(
                            entries = CollectionMatchers.eachOnce(
                                elements = setOf(
                                    UnorderedTupleConstructorMatcher.EntryMatcher(
                                        name = Matcher.Irrelevant(),
                                        value = IntLiteralMatcher(
                                            value = Matcher.Equals(3L),
                                        ).checked(),
                                    ),
                                    UnorderedTupleConstructorMatcher.EntryMatcher(
                                        name = Matcher.Irrelevant(),
                                        value = CallMatcher(
                                            subject = ReferenceMatcher(
                                                referredDeclaration = Matcher.Equals(argumentDeclaration),
                                            ).checked(),
                                            argument = IntLiteralMatcher(
                                                value = Matcher.Equals(0L),
                                            ).checked(),
                                        ).checked(),
                                    ),
                                ),
                            ),
                        ).checked(),
                    ).checked(),
                ),
                actual = abstractionConstructor,
            )
        }

        @Test
        fun testMultipleArguments() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    ^{a: Int, b: Bool, c: String} -> Int => %let {
                        x = f[a, b],
                        y = g{p: x, q: h[b, c]},
                    } %in y
                """.trimIndent(),
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructorBuildOutput = AbstractionConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = BuiltinScope,
                    outerScope = FakeStaticScope(
                        declarations = setOf(
                            FakeUserDeclaration(
                                name = Identifier.of("f"),
                                declaredType = UniversalFunctionType(
                                    argumentType = OrderedTupleType(
                                        elements = listOf(
                                            OrderedTupleType.Element(
                                                name = null,
                                                type = IntCollectiveType,
                                            ),
                                            OrderedTupleType.Element(
                                                name = null,
                                                type = BoolType,
                                            ),
                                        ),
                                    ),
                                    imageType = IntCollectiveType,
                                ),
                            ),
                            FakeUserDeclaration(
                                name = Identifier.of("g"),
                                declaredType = UniversalFunctionType(
                                    argumentType = UnorderedTupleType.fromEntries(
                                        UnorderedTupleType.Entry(
                                            name = Symbol.of("p"),
                                            type = IntCollectiveType,
                                        ),
                                        UnorderedTupleType.Entry(
                                            name = Symbol.of("q"),
                                            type = IntCollectiveType,
                                        ),
                                    ),
                                    imageType = IntCollectiveType,
                                ),
                            ),
                            FakeUserDeclaration(
                                name = Identifier.of("h"),
                                declaredType = UniversalFunctionType(
                                    argumentType = OrderedTupleType(
                                        elements = listOf(
                                            OrderedTupleType.Element(
                                                name = null,
                                                type = BoolType,
                                            ),
                                            OrderedTupleType.Element(
                                                name = null,
                                                type = StringType,
                                            ),
                                        ),
                                    ),
                                    imageType = IntCollectiveType,
                                ),
                            ),
                        ),
                    ),
                ),
                term = term,
            )

            val abstractionConstructor = abstractionConstructorBuildOutput.expression
            val argumentDeclarationBlock = abstractionConstructorBuildOutput.argumentDeclarationBlock

            fun getResolvedArgument(name: String) = assertIs<ResolvedUnorderedArgument>(
                argumentDeclarationBlock.resolveNameLocally(
                    name = Identifier.of(name = name),
                ),
            )

            fun buildReferenceMatcher(
                resolvedArgument: ResolvedUnorderedArgument,
            ): Matcher<Expression> = resolvedArgument.buildReferenceMatcher()

            val aResolvedArgument = getResolvedArgument(name = "a")
            val bResolvedArgument = getResolvedArgument(name = "b")
            val cResolvedArgument = getResolvedArgument(name = "c")

            val fCall = CallMatcher(
                subject = Matcher.Irrelevant(),
                argument = OrderedTupleConstructorMatcher(
                    elements = ListMatchers.inOrder(
                        buildReferenceMatcher(aResolvedArgument),
                        buildReferenceMatcher(bResolvedArgument),
                    ),
                ).checked(),
            )

            val hCall = CallMatcher(
                subject = Matcher.Irrelevant(),
                argument = OrderedTupleConstructorMatcher(
                    elements = ListMatchers.inOrder(
                        buildReferenceMatcher(bResolvedArgument),
                        buildReferenceMatcher(cResolvedArgument),
                    ),
                ).checked(),
            )

            val gCall = CallMatcher(
                subject = Matcher.Irrelevant(),
                argument = UnorderedTupleConstructorMatcher(
                    entries = CollectionMatchers.eachOnce(
                        UnorderedTupleConstructorMatcher.EntryMatcher(
                            name = Matcher.Equals(Symbol.of("p")),
                            value = fCall.checked(),
                        ),
                        UnorderedTupleConstructorMatcher.EntryMatcher(
                            name = Matcher.Equals(Symbol.of("q")),
                            value = hCall.checked(),
                        ),
                    ),
                ).checked(),
            )

            assertMatches(
                matcher = AbstractionConstructorMatcher(
                    argumentType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("a")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("b")),
                                type = Matcher.Is<BoolType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("c")),
                                type = Matcher.Is<StringType>(),
                            ),
                        ),
                    ).checked(),
                    declaredImageType = Matcher.Is<IntCollectiveType>(),
                    image = gCall.checked(),
                ),
                actual = abstractionConstructor,
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
                context = Expression.BuildContext.Builtin,
                term = term,
            ).expression

            val inferredType = assertIs<FunctionType>(
                value = abstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )

            assertIs<BoolType>(
                value = inferredType.imageType,
            )
        }

        @Test
        fun testWithoutDeclaredImageType_fromBodyOnly() {
            val term = ExpressionSourceTerm.parse(
                // Image type has to be inferred from the body only
                source = "^[a: Int] => 2 + 3",
            ) as AbstractionConstructorSourceTerm

            val abstractionConstructor = AbstractionConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).expression

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
                context = Expression.BuildContext.Builtin,
                term = term,
            ).expression

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
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val type = (expression as Expression).inferredTypeOrIllType.getOrCompute()

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Identifier.of("n"),
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
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Identifier.of("f"),
                ),
            )

            val fType = assertIs<FunctionType>(
                value = fDefinition.computedBodyType.getOrCompute(),
            )

            assertIs<IntType>(value = fType.imageType)

            val gDefinition = assertNotNull(
                actual = let.definitionBlock.getValueDefinition(
                    name = Identifier.of("g"),
                ),
            )

            val gType = assertIs<FunctionType>(
                value = gDefinition.computedBodyType.getOrCompute(),
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
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Identifier.of("f"),
                ),
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType.Empty,
                    imageType = IllType,
                ),
                actual = fDefinition.computedBodyType.getOrCompute(),
            )

            val gDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Identifier.of("g"),
                ),
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType.Empty,
                    imageType = IllType,
                ),
                actual = gDefinition.computedBodyType.getOrCompute(),
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
                context = Expression.BuildContext.Builtin,
                term = term,
            ).expression

            val classifiedValue = abstractionConstructor.classified

            val constValue = assertIs<ConstExpression>(
                classifiedValue,
            )

            assertIs<ComputableAbstraction>(
                constValue.valueThunk.value
            )
        }

        @Test
        fun testReferringConst() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        a = 2,
                        f = ^[b: Int] => a * b,
                    } %in f
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val letExpression = LetExpression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val fDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Identifier.of("f"),
                ),
            )

            val classifiedValue = fDefinition.body.classified

            val constValue = assertIs<ConstExpression>(
                classifiedValue,
            )

            val fValue = assertIs<ComputableAbstraction>(
                constValue.valueThunk.value
            )

            assertEquals(
                expected = IntValue(10L),
                actual = fValue.applyOrdered(IntValue(5L)).value,
            )
        }

        @Test
        fun testSelfRecursive() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %let {
                        fib = ^[n: Int] => %if n == 0 (
                            %then 0,
                            %else %if n == 1 (
                                %then 1,
                                %else fib[n - 1] + fib[n - 2],
                            ),
                        ),
                    } %in fib
                """.trimIndent(),
            ) as LetExpressionSourceTerm

            val letExpression = LetExpression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val fibDefinition = assertNotNull(
                actual = letExpression.definitionBlock.getValueDefinition(
                    name = Identifier.of("fib"),
                ),
            )

            val classifiedExpression = fibDefinition.body.classified

            val constValue = assertIs<ConstExpression>(
                classifiedExpression,
            )

            val fValue = assertIs<ComputableAbstraction>(
                constValue.valueThunk.value
            )

            assertEquals(
                expected = IntValue(3L), // 3 is a 4th Fibonacci number
                actual = fValue.applyOrdered(IntValue(4L)).value,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstractionConstructor = AbstractionConstructor.build(
                context = Expression.BuildContext.Builtin, term = ExpressionSourceTerm.parse(
                    source = "^[n: Int, m: Int] => n * m",
                ) as AbstractionConstructorSourceTerm
            ).expression

            val result = assertIs<EvaluationResult<Value>>(
                abstractionConstructor.bind(
                    dynamicScope = DynamicScope.Empty,
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

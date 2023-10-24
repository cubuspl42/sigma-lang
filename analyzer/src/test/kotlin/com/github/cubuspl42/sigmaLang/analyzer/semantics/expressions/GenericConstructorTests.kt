@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeAnnotatedBody
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import utils.assertMatches
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericConstructorTerm
import utils.CollectionMatchers
import utils.ListMatchers
import utils.checked
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenericConstructorTests {
    class ConstructionTests {
        @Test
        fun testLetBody() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    ^[a: Type] !=> %let {
                        result: ^[Type...] = [],
                    } %in result
                """.trimIndent(),
            ) as GenericConstructorTerm

            val genericConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            assertEquals(
                expected = emptySet(),
                actual = genericConstructor.errors,
            )

            assertMatches(
                matcher = GenericConstructorMatcher(
                    metaArgumentTypeConstructor = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("a")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                    body = Matcher.Is<TypeAnnotatedBody>(),
                ),
                actual = genericConstructor,
            )
        }

        @Test
        fun testAbstractionBody() {
            val term = ExpressionSourceTerm.parse(
                source = "^[t: Type] !=> ^[a: t] -> Int => 11",
            ) as GenericConstructorTerm

            val metaAbstractionConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            assertMatches(
                matcher = GenericConstructorMatcher(
                    metaArgumentTypeConstructor = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("t")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                    body = AbstractionConstructorMatcher(
                        argumentType = OrderedTupleTypeMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.Equals(expected = Identifier.of("a")),
                                    type = Matcher.Is<TypeVariable>(),
                                ),
                            ),
                        ).checked(),
                        declaredImageType = Matcher.Is<IntCollectiveType>(),
                        image = IntLiteralMatcher(
                            value = Matcher.Equals(11L),
                        ).checked(),
                    ).checked(),
                ),
                actual = metaAbstractionConstructor,
            )
        }
    }

    class TypeInferenceTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "^[t: Type] !=> ^[a: t] -> Int => 11",
            ) as GenericConstructorTerm

            val metaAbstractionConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            assertMatches(
                matcher = GenericTypeMatcher(
                    metaArgumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("t")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                ).checked(),
                actual = metaAbstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testWithDeclaredImageType_fromMetaArgument() {
            val term = ExpressionSourceTerm.parse(
                // The declared image type is an introduced meta-argument
                source = """
                    ^[e: Type] !=> ^[a: e] -> e => %let {
                        result: e = a,
                    } %in result
                """.trimIndent(),
            )

            val expression = Expression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            assertMatches(
                matcher = GenericTypeMatcher(
                    metaArgumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("e")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked()
                ).checked(),
                actual = expression.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testWithDeclaredImageType_fromMetaArgumentComplex() {
            val term = ExpressionSourceTerm.parse(
                // The declared image type is a complex expression based on a meta-argument
                source = """
                        ^[
                            t: ^{t1: Type, t2: Type},
                        ] !=> ^[
                            l: ^[t.t1...],
                            f: ^[t.t1] -> t.t2,
                        ] -> ^[t.t2...] => %let {
                            result: ^[t.t2...] = map[l, f]
                        } %in result
                    """.trimIndent(),
            )

            val expression = Expression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                expression.errors,
            )

            assertMatches(
                matcher = GenericTypeMatcher(
                    metaArgumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("t")),
                                type = UnorderedTupleTypeMatcher(
                                    entries = CollectionMatchers.eachOnce(
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("t1")),
                                            type = Matcher.Is<TypeType>(),
                                        ),
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("t2")),
                                            type = Matcher.Is<TypeType>(),
                                        ),
                                    ),
                                ).checked(),
                            ),
                        ),
                    ).checked(),
                ).checked(),
                actual = expression.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {

        }
    }
}

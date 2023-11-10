@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeAnnotatedBody
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericConstructorTerm
import utils.CollectionMatchers
import utils.FakeDefinition
import utils.FakeDefinitionBlock
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
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
                    metaArgumentType = OrderedTupleTypeMatcher(
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
                    metaArgumentType = OrderedTupleTypeMatcher(
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
        fun testSimple() {
            val term = ExpressionSourceTerm.parse(
                source = "^[t: Type] !=> ^[a: t] -> Int => 11",
            ) as GenericConstructorTerm

            val genericConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("a")),
                                type = Matcher.Is<TypeVariable>(),
                            ),
                        ),
                    ).checked(),
                    imageType = Matcher.Is<IntCollectiveType>(),
                ).checked(),
                actual = genericConstructor.body.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        @Ignore // TODO: Trait meta-static analysis
        fun testNonTrait() {
            val term = ExpressionSourceTerm.parse(
                source = "^{t: Type, n: Int} !=> 2",
            ) as GenericConstructorTerm

            val genericConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            // - ensure that meta arguments are "traits"
            //   - trait is a TypeType or a tuple of traits

            // TODO: Specify the error
            assertMatches(
                matcher = CollectionMatchers.isNotEmpty(),
                actual = genericConstructor.errors,
            )
        }

        @Test
        fun testWithDeclaredImageType() {
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
                    parameterType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("e")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                    bodyType = UniversalFunctionTypeMatcher(
                        argumentType = OrderedTupleTypeMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("a")),
                                    type = Matcher.Is<TypeVariable>(),
                                ),
                            ),
                        ).checked(),
                        imageType = Matcher.Is<TypeVariable>(),
                    ).checked(),
                ).checked(),
                actual = expression.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testComplex() {
            // TODO:

            // - make tuple type values a separate class and make them field-readable?
            //   - ...a separate thing? They still have Type inside


            // - bring back the dynamic translation scope?
            // - TraitDynamicScope translate trait declarations to trait values (dicts/new tuple type values; TVs)
            // - How to "evaluate" the type with TVs in `specify`?
            //   - new algorithm?
            //   - convert to expression?

            val term = ExpressionSourceTerm.parse(
                source = """
                         ^[
                             t: ^{
                                t1: Type,
                                t2: Type,
                             },
                         ] !=> ^{
                            a: t.t1,
                            b: t.t2,
                         } => 1
                    """.trimIndent(),
            ) as GenericConstructorTerm

            val genericConstructor = GenericConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).value

            fun buildBodyTypeMatcher(
                t1Matcher: Matcher<Type>,
                t2Matcher: Matcher<Type>,
            ) = UniversalFunctionTypeMatcher(
                argumentType = UnorderedTupleTypeMatcher(
                    entries = CollectionMatchers.eachOnce(
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(expected = Identifier.of("a")),
                            type = t1Matcher.checked(),
                        ),
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(expected = Identifier.of("b")),
                            type = t2Matcher.checked(),
                        ),
                    ),
                ).checked(),
                imageType = IntLiteralTypeMatcher(
                    value = Matcher.Equals(1L),
                ).checked(),
            )

            val genericType = assertIs<GenericType>(
                genericConstructor.inferredTypeOrIllType.getOrCompute()
            )

            assertMatches(
                matcher = GenericTypeMatcher(
                    parameterType = OrderedTupleTypeMatcher(
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
                    bodyType = buildBodyTypeMatcher(
                        t1Matcher = Matcher.Is<TypeVariable>(),
                        t2Matcher = Matcher.Is<TypeVariable>(),
                    ).checked(),
                ),
                actual = genericType,
            )

            val specifiedType = genericType.parametrize(
                ArrayTable(
                    DictValue(
                        valueByKey = mapOf(
                            Identifier.of("t1") to IntCollectiveType.asValue,
                            Identifier.of("t2") to StringType.asValue,
                        ),
                    ),
                ),
            )

            assertMatches(
                matcher = buildBodyTypeMatcher(
                    t1Matcher = Matcher.Is<IntCollectiveType>(),
                    t2Matcher = Matcher.Is<StringType>(),
                ).checked(),
                actual = specifiedType,
            )
        }

        @Test
        fun testWithTraitReference() {
            val term = ExpressionSourceTerm.parse(
                source = """
                        ^[
                            t: Trait1,
                        ] !=> 0
                    """.trimIndent(),
            )

            val expression = Expression.build(
                context = Expression.BuildContext(
                    outerScope = FakeDefinitionBlock(
                        level = StaticScope.Level.Meta, // TODO: Shouldn't this be meta-meta?
                        definitions = setOf(
                            FakeDefinition(
                                name = Identifier.of("Trait1"),
                                type = TypeType,
                                value = UnorderedTupleType(
                                    valueTypeByName = mapOf(
                                        Identifier.of("t1") to TypeType,
                                        Identifier.of("t2") to TypeType,
                                    )
                                ).asValue,
                            ),
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                expression.errors,
            )

            val type = assertIs<GenericType>(
                expression.inferredTypeOrIllType.getOrCompute()
            )

            assertMatches(
                matcher = GenericTypeMatcher(
                    parameterType = OrderedTupleTypeMatcher(
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
                    bodyType = Matcher.Irrelevant(),
                ),
                actual = type,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {

        }
    }
}

@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import utils.CollectionMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test

class GenericTypeTests {
    class ParametrizationTests {
        @Test
        fun testSimple() {
            val parameterDeclaration = AbstractionConstructor.ArgumentDeclaration(
                declaredType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("x") to TypeType
                    ),
                ),
            )

            val xTypeVariable = TypeVariable(
                traitDeclaration = parameterDeclaration,
                path = TypeVariable.Path.Root.extend(Identifier.of("x")),
            )

            val genericType = GenericType(
                parameterDeclaration = parameterDeclaration,
                bodyType = UniversalFunctionType(
                    argumentType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("xArg") to xTypeVariable,
                        ),
                    ),
                    imageType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("xField") to xTypeVariable,
                            Identifier.of("boolField") to BoolType,
                        ),
                    ),
                ),
            )

            val parametrizedType = genericType.parametrize(
                metaArgument = DictValue(
                    valueByKey = mapOf(
                        Identifier.of("x") to StringType.asValue,
                    ),
                ),
            )

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("xArg")),
                                type = Matcher.Is<StringType>(),
                            ),
                        ),
                    ).checked(),
                    imageType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("xField")),
                                type = Matcher.Is<StringType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("boolField")),
                                type = Matcher.Is<BoolType>(),
                            ),
                        )
                    ).checked(),
                ).checked(),
                actual = parametrizedType,
            )
        }

        @Test
        fun testPartial() {
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

            val genericType = GenericType(
                parameterDeclaration = parameterDeclaration,
                bodyType = UniversalFunctionType(
                    argumentType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("xArg") to xTypeVariable,
                            Identifier.of("yArg") to xTypeVariable,
                        ),
                    ),
                    imageType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("xField") to xTypeVariable,
                            Identifier.of("yField") to yTypeVariable,
                        ),
                    ),
                ),
            )

            val parametrizedType = genericType.parametrize(
                metaArgument = DictValue(
                    valueByKey = mapOf(
                        Identifier.of("x") to StringType.asValue,
                    ),
                ),
            )

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("xArg")),
                                type = Matcher.Is<StringType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("yArg")),
                                type = Matcher.Is<StringType>(),
                            ),
                        ),
                    ).checked(),
                    imageType = UnorderedTupleTypeMatcher(
                        entries = CollectionMatchers.eachOnce(
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("xField")),
                                type = Matcher.Is<StringType>(),
                            ),
                            UnorderedTupleTypeMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("yField")),
                                type = Matcher.Equals(
                                    TypePlaceholder(
                                        typeVariable = yTypeVariable,
                                    ),
                                ),
                            ),
                        )
                    ).checked(),
                ).checked(),
                actual = parametrizedType,
            )
        }
    }
}

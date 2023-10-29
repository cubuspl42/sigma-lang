package com.github.cubuspl42.sigmaLang.analyzer.semantics

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm
import utils.CollectionMatchers
import utils.Matcher
import utils.assertMatches
import utils.assertTypeIsEquivalent
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ClassDefinitionTests {
    class TypeCheckingTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = """
                    %class Foo ^{
                        foo: Int,
                        bar: Bool,
                    }
                """.trimIndent(),
            ) as ClassDefinitionTerm

            val classTag = QualifiedPath.of("foo", "Foo")

            val classDefinition = ClassDefinition.build(
                context = Expression.BuildContext.Builtin,
                qualifiedPath = classTag,
                term = term,
            )

            val classType = classDefinition.definition.computedBodyType.getOrCompute() as SpecificType


//            assertTypeIsEquivalent(
//                expected = UnorderedTupleType(
//                    valueTypeByName = mapOf(
//                        ClassDefinition.classTagKey to tagType,
//                        ClassDefinition.instanceTypeKey to TypeType,
//                        Identifier.of("new") to UniversalFunctionType(
//                            argumentType = UnorderedTupleType(
//                                valueTypeByName = mapOf(
//                                    Identifier.of("foo") to IntCollectiveType,
//                                    Identifier.of("bar") to BoolType,
//                                )
//                            ),
//                            imageType = UnorderedTupleType(
//                                valueTypeByName = mapOf(
//                                    ClassDefinition.instanceTagKey to tagType,
//                                    Identifier.of("foo") to IntCollectiveType,
//                                    Identifier.of("bar") to BoolType,
//                                )
//                            ),
//                        )
//                    ),
//                ),
//                actual = classType,
//            )

            assertMatches(
                matcher = UnorderedTupleTypeMatcher(
                    entries = CollectionMatchers.eachOnce(
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(ClassDefinition.classTagKey),
                            type = Matcher.Equals(SymbolType(value = classTag)),
                        ),
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(Identifier.of("type")),
                            type = Matcher.Is<TypeType>(),
                        ),
                        UnorderedTupleTypeMatcher.EntryMatcher(
                            name = Matcher.Equals(Identifier.of("new")),
                            type = UniversalFunctionTypeMatcher(
                                argumentType = UnorderedTupleTypeMatcher(
                                    entries = CollectionMatchers.eachOnce(
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("foo")),
                                            type = Matcher.Is<IntCollectiveType>(),
                                        ),
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("bar")),
                                            type = Matcher.Is<BoolType>(),
                                        ),
                                    ),
                                ).checked(),
                                imageType = UnorderedTupleTypeMatcher(
                                    entries = CollectionMatchers.eachOnce(
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(ClassDefinition.instanceTagKey),
                                            type = Matcher.Equals(SymbolType(value = classTag)),
                                        ),
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("foo")),
                                            type = Matcher.Is<IntCollectiveType>(),
                                        ),
                                        UnorderedTupleTypeMatcher.EntryMatcher(
                                            name = Matcher.Equals(Identifier.of("bar")),
                                            type = Matcher.Is<BoolType>(),
                                        ),
                                    ),
                                ).checked(),
                            ).checked(),
                        ),
                    ),
                ).checked(),
                actual = classType,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = """
                    %class Foo ^{
                        foo: Int,
                        bar: Bool,
                    }
                """.trimIndent(),
            ) as ClassDefinitionTerm

            val classDefinition = ClassDefinition.build(
                context = Expression.BuildContext.Builtin,
                qualifiedPath = QualifiedPath(
                    segments = listOf(
                        Identifier.of("foo"),
                        Identifier.of("Foo"),
                    ),
                ),
                term = term,
            ).definition

            val classValue = assertIs<DictValue>(
                assertNotNull(
                    classDefinition.valueThunk.value
                )
            )

            assertEquals(
                expected = 3,
                actual = classValue.thunkByKey.size,
            )

            assertEquals(
                expected = QualifiedPath.of("foo", "Foo"),
                actual = classValue.readValue(
                    key = ClassDefinition.classTagKey,
                ),
            )

            val actualType = assertNotNull(
                actual = classValue.readValue(
                    key = ClassDefinition.instanceTypeKey,
                )?.asType,
            ) as SpecificType

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        ClassDefinition.instanceTagKey to SymbolType(
                            value = QualifiedPath.of("foo", "Foo"),
                        ),
                        Identifier.of("foo") to IntCollectiveType,
                        Identifier.of("bar") to BoolType,
                    ),
                ),
                actual = actualType,
            )

            assertIs<FunctionValue>(
                classValue.readValue(
                    key = Identifier.of("new"),
                ),
            )
        }
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

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
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm
import utils.assertTypeIsEquivalent
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ClassDefinitionTests {
    class TypeCheckingTests {
        @Test
        @Ignore // TODO: Re-support classes
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
                        Identifier.of("Foo"),
                    ),
                ),
                term = term,
            )

            val classType = classDefinition.computedBodyType.getOrCompute() as SpecificType

            val tagType = SymbolType.of("Foo")

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        ClassDefinition.classTagKey to tagType,
                        ClassDefinition.instanceTypeKey to TypeType,
                        Identifier.of("new") to UniversalFunctionType(
                            argumentType = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    Identifier.of("foo") to IntCollectiveType,
                                    Identifier.of("bar") to BoolType,
                                )
                            ),
                            imageType = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    ClassDefinition.instanceTagKey to tagType,
                                    Identifier.of("foo") to IntCollectiveType,
                                    Identifier.of("bar") to BoolType,
                                )
                            ),
                        )
                    ),
                ),
                actual = classType,
            )

        }
    }

    class EvaluationTests {
        @Test
        @Ignore // TODO: Re-support classes
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
            )

            val classValue = assertIs<DictValue>(
                assertNotNull(
                    classDefinition.valueThunk.value
                )
            )

            assertEquals(
                expected = 3,
                actual = classValue.entries.size,
            )

            assertEquals(
                expected = Identifier.of("foo.Foo"),
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
                        ClassDefinition.instanceTagKey to SymbolType.of("foo.Foo"),
                        Identifier.of("foo") to IntCollectiveType,
                        Identifier.of("bar") to BoolType,
                    )
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

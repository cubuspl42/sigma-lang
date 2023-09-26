package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm
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

            val classDefinition = ClassDefinition.build(
                outerScope = BuiltinScope,
                qualifiedPath = QualifiedPath(
                    segments = listOf(
                        Symbol.of("Foo"),
                    ),
                ),
                term = term,
            )

            val classType = classDefinition.computedEffectiveType.getOrCompute()

            val tagType = SymbolType.of("Foo")

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        ClassDefinition.classTagKey to tagType,
                        ClassDefinition.classTypeKey to MetaType,
                        Symbol.of("new") to UniversalFunctionType(
                            argumentType = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    Symbol.of("foo") to IntCollectiveType,
                                    Symbol.of("bar") to BoolType,
                                )
                            ),
                            imageType = UnorderedTupleType(
                                valueTypeByName = mapOf(
                                    ClassDefinition.instanceTagKey to tagType,
                                    Symbol.of("foo") to IntCollectiveType,
                                    Symbol.of("bar") to BoolType,
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
                outerScope = BuiltinScope,
                qualifiedPath = QualifiedPath(
                    segments = listOf(
                        Symbol.of("foo"),
                        Symbol.of("Foo"),
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
                expected = Symbol.of("foo.Foo"),
                actual = classValue.read(
                    key = ClassDefinition.classTagKey,
                ),
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        ClassDefinition.instanceTagKey to SymbolType.of("foo.Foo"),
                        Symbol.of("foo") to IntCollectiveType,
                        Symbol.of("bar") to BoolType,
                    )
                ).asValue,
                actual = classValue.read(
                    key = ClassDefinition.classTypeKey,
                ),
            )

            assertIs<FunctionValue>(
                classValue.read(
                    key = Symbol.of("new"),
                ),
            )
        }
    }
}

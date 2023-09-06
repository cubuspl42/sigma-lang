package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
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
                term = term,
            )

            val bodyType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val classType = classDefinition.effectiveTypeThunk.value

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("type") to MetaType,
                        Symbol.of("new") to UniversalFunctionType(
                            argumentType = bodyType,
                            imageType = bodyType,
                        ),
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
                term = term,
            )

            val bodyType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("foo") to IntCollectiveType,
                    Symbol.of("bar") to BoolType,
                )
            )

            val classValue = assertIs<DictValue>(
                assertNotNull(
                    classDefinition.valueThunk.value
                )
            )

            assertEquals(
                expected = 2,
                actual = classValue.entries.size,
            )

            assertEquals(
                expected = bodyType.asValue,
                actual = classValue.read(
                    key = Symbol.of("type"),
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

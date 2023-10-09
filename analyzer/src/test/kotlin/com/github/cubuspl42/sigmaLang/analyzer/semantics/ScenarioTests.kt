package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import utils.assertTypeIsEquivalent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ScenarioTests {
    @Test
    fun testGenericClass() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const Entry = ^[valueType: Type] => ^{
                        key: Int,
                        value: valueType,
                    }
                    
                    %const entryOf = !^[valueType: Type] ^{
                        key: Int,
                        value: valueType,
                    } -> Entry[valueType] => {
                        key: key,
                        value: value,
                    }
                    
                    %const entryTrueOf = ^{
                        key: Int,
                    } -> Entry[Bool] => {
                        key: key,
                        value: true,
                    }
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            outerScope = Project.loadPrelude().innerStaticScope,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespaceDefinition.errors,
        )

        // Validate `Entry`

        val entryTypeConstructorDefinition = namespaceDefinition.getDefinition(
            name = Symbol.of("Entry"),
        )!!

        assertEquals(
            expected = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("valueType"),
                            type = TypeType,
                        ),
                    ),
                ),
                imageType = TypeType,
            ),
            actual = entryTypeConstructorDefinition.computedEffectiveType.getOrCompute(),
        )

        // Construct `Entry[Bool]` and validate it

        val entryTypeConstructorValue = entryTypeConstructorDefinition.valueThunk.value

        assertIs<FunctionValue>(entryTypeConstructorValue)

        val entryType = assertNotNull(
            actual = entryTypeConstructorValue.apply(
                ArrayTable(
                    elements = listOf(
                        BoolType.asValue,
                    ),
                ),
            ).value?.asType,
        )

        assertTypeIsEquivalent(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("key") to IntCollectiveType,
                    Symbol.of("value") to BoolType,
                ),
            ),
            actual = entryType,
        )

        // Validate `entryOf`

        val entryOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Symbol.of("entryOf"),
        )!!

        assertTypeIsEquivalent(
            expected = UniversalFunctionType(
                metaArgumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("valueType"),
                            type = TypeType,
                        ),
                    ),
                ),
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key") to IntCollectiveType,
                        Symbol.of("value") to TypeVariable.of("valueType"),
                    ),
                ),
                imageType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key") to IntCollectiveType,
                        Symbol.of("value") to TypeVariable.of("valueType"),
                    ),
                ),
            ),
            actual = entryOfAbstractionDefinition.computedEffectiveType.getOrCompute(),
        )

        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Symbol.of("entryTrueOf"),
        )!!

        assertTypeIsEquivalent(
            expected = UniversalFunctionType(
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key") to IntCollectiveType,
                    ),
                ),
                imageType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key") to IntCollectiveType,
                        Symbol.of("value") to BoolType,
                    ),
                ),
            ),
            actual = entryTrueOfAbstractionDefinition.computedEffectiveType.getOrCompute(),
        )
    }

    @Test
    fun testNonInferableGenericFunctionCall() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const f = !^[type: Type] ^[] -> ^[type...] => 0
                    
                    %const a = f[]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            outerScope = BuiltinScope,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        namespaceDefinition.printErrors()

        val error = assertIs<Call.NonFullyInferredCalleeTypeError>(
            namespaceDefinition.errors.singleOrNull(),
        )

        assertEquals(
            expected = setOf(
                TypeVariable.of("type")
            ),
            actual = error.nonInferredTypeVariables,
        )

        val aType = namespaceDefinition.getDefinition(
            name = Symbol.of("a"),
        )!!.computedEffectiveType.getOrCompute()

        assertEquals(
            expected = IllType,
            actual = aType,
        )
    }

    @Test
    fun testNestedGenericFunctions() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const f = !^[aType: Type] ^[a: aType] -> Int => %let {
                        g = !^[bType: Type, cType: Type] ^[a: aType, b: bType, c: cType] -> Int => 0,
                    } %in g[a, false, {}]
                    
                    %const a = f[1]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            outerScope = BuiltinScope,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespaceDefinition.errors,
        )
    }
}

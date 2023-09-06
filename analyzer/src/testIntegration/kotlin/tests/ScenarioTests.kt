package tests

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
                    
                    %const entryOf = ![valueType] ^{
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
                            type = MetaType,
                        ),
                    ),
                ),
                imageType = MetaType,
            ),
            actual = entryTypeConstructorDefinition.effectiveTypeThunk.value,
        )

        // Construct `Entry[Bool]` and validate it

        val entryTypeConstructorValue = entryTypeConstructorDefinition.valueThunk.value

        assertIs<FunctionValue>(entryTypeConstructorValue)

        val entryTypeValue = entryTypeConstructorValue.apply(
            ArrayTable(
                elements = listOf(
                    BoolType.asValue,
                ),
            ),
        ).value

        assertEquals(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("key") to IntCollectiveType,
                    Symbol.of("value") to BoolType,
                ),
            ).asValue,
            actual = entryTypeValue,
        )

        // Validate `entryOf`

        val entryOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Symbol.of("entryOf"),
        )!!

        assertEquals(
            expected = UniversalFunctionType(
                genericParameters = setOf(
                    TypeVariable.of("valueType"),
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
            actual = entryOfAbstractionDefinition.effectiveTypeThunk.value,
        )

        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = namespaceDefinition.getDefinition(
            name = Symbol.of("entryTrueOf"),
        )!!

        assertEquals(
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
            actual = entryTrueOfAbstractionDefinition.effectiveTypeThunk.value,
        )
    }

    @Test
    fun testNonInferableGenericFunctionCall() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                namespace EntryNamespace (
                    %const f = ![type] ^[] -> ^[type...] => 0
                    
                    %const a = f[]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            outerScope = BuiltinScope,
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
        )!!.effectiveTypeThunk.value

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
                    %const f = ![aType] ^[a: aType] -> Int => %let {
                        g = ![bType, cType] ^[a: aType, b: bType, c: cType] -> Int => 0,
                    } %in g[a, false, {}]
                    
                    %const a = f[1]
                )
            """.trimIndent(),
        )

        val namespaceDefinition = NamespaceDefinition.build(
            outerScope = BuiltinScope,
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespaceDefinition.errors,
        )
    }
}

package tests

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Namespace
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Prelude
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
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

        val namespace = Namespace.build(
            prelude = Prelude.load(),
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespace.errors,
        )

        // Validate `Entry`

        val entryTypeConstructorDefinition = namespace.getEntry(
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
            actual = entryTypeConstructorDefinition.effectiveType.value,
        )

        // Construct `Entry[Bool]` and validate it

        val entryTypeConstructorValue = entryTypeConstructorDefinition.valueThunk.value

        assertIs<FunctionValue>(entryTypeConstructorValue)

        val entryTypeValue = entryTypeConstructorValue.apply(
            ArrayTable(
                elements = listOf(
                    BoolType,
                ),
            ),
        ).value

        assertEquals(
            expected = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("key") to IntCollectiveType,
                    Symbol.of("value") to BoolType,
                ),
            ),
            actual = entryTypeValue,
        )

        // Validate `entryOf`

        val entryOfAbstractionDefinition = namespace.getEntry(
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
            actual = entryOfAbstractionDefinition.effectiveType.value,
        )

        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = namespace.getEntry(
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
            actual = entryTrueOfAbstractionDefinition.effectiveType.value,
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

        val namespace = Namespace.build(
            prelude = Prelude.load(),
            term = term,
        )

        namespace.printErrors()

        val error = assertIs<Call.NonFullyInferredCalleeTypeError>(
            namespace.errors.singleOrNull(),
        )

        assertEquals(
            expected = setOf(
                TypeVariable.of("type")
            ),
            actual = error.nonInferredTypeVariables,
        )

        val aType = namespace.getEntry(
            name = Symbol.of("a"),
        )!!.effectiveType.value

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

        val namespace = Namespace.build(
            prelude = Prelude.load(),
            term = term,
        )

        assertEquals(
            expected = emptySet(),
            actual = namespace.errors,
        )
    }
}

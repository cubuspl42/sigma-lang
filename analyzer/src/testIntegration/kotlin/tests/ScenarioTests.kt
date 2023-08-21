package tests

import sigma.evaluation.values.ArrayTable
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Symbol
import sigma.semantics.Namespace
import sigma.semantics.Prelude
import sigma.semantics.expressions.Call
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.MetaType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.NamespaceDefinitionSourceTerm
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

        val entryTypeConstructorDefinition = namespace.getConstantDefinition(
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
            actual = entryTypeConstructorDefinition.asValueDefinition.effectiveValueType.value,
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

        val entryOfAbstractionDefinition = namespace.getConstantDefinition(
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
            actual = entryOfAbstractionDefinition.asValueDefinition.effectiveValueType.value,
        )

        // Validate `entryTrueOf`

        val entryTrueOfAbstractionDefinition = namespace.getConstantDefinition(
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
            actual = entryTrueOfAbstractionDefinition.asValueDefinition.effectiveValueType.value,
        )
    }

    @Test
    fun testNonInferableGenericFunctionCall() {
        val term = NamespaceDefinitionSourceTerm.parse(
            source = """
                namespace EntryNamespace (
                    %const f = ![type] ^[] -> ^[type*] => 0
                    
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

        val aType = namespace.getConstantDefinition(
            name = Symbol.of("a"),
        )!!.asValueDefinition.effectiveValueType.value

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

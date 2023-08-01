package tests

import sigma.evaluation.values.ArrayTable
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Symbol
import sigma.semantics.Namespace
import sigma.semantics.Prelude
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.MetaType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.NamespaceDefinitionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenericClassTests {
    @Test
    fun test() {
        val term = NamespaceDefinitionTerm.parse(
            source = """
                namespace EntryNamespace (
                    const Entry = ^[valueType: Type] => ^{
                        key: Int,
                        value: valueType,
                    }
                    
                    const entryOf = ![valueType] ^{
                        key: Int,
                        value: valueType,
                    } -> Entry[valueType] => {
                        key: key,
                        value: value,
                    }
                    
                    const entryTrueOf = ^{
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
}
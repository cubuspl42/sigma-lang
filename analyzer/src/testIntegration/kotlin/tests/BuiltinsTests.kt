package tests

import sigma.evaluation.values.ArrayTable
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.SetValue
import sigma.evaluation.values.Symbol
import sigma.semantics.Namespace
import sigma.semantics.Prelude
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.MetaType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.SetType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.NamespaceDefinitionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BuiltinsTests {
    @Test
    fun testSet() {
        val term = NamespaceDefinitionTerm.parse(
            source = """
                %namespace EntryNamespace (
                    %const mySet1 = setOf[[1, 2, 3]]
                    
                    %const contains2 = setContains[mySet1, 2]

                    %const contains5 = setContains[mySet1, 5]
                    
                    %const mySet2 = setUnion[mySet1, setOf[[2, 3, 4]]]
                )
            """.trimIndent(),
        )

        val namespace = Namespace.build(
            prelude = Prelude.load(),
            term = term,
        )

        // FIXME: Fix incorrect type variable resolution
//        assertEquals(
//            expected = emptySet(),
//            actual = namespace.errors,
//        )

        // Validate `mySet1`

        val mySet1Definition = namespace.getConstantDefinition(
            name = Symbol.of("mySet1"),
        )!!

        assertEquals(
            expected = SetType(
                elementType = IntCollectiveType,
            ),
            actual = mySet1Definition.asValueDefinition.effectiveValueType.value,
        )

        assertEquals(
            expected = SetValue(
                elements = setOf(
                    IntValue(value = 1L),
                    IntValue(value = 2L),
                    IntValue(value = 3L),
                ),
            ),
            actual = mySet1Definition.staticValue.value,
        )

        // Validate `contains2`

        val contains2Definition = namespace.getConstantDefinition(
            name = Symbol.of("contains2"),
        )!!

        assertEquals(
            expected = BoolValue(value = true),
            actual = contains2Definition.staticValue.value,
        )

        // Validate `contains5`

        val contains5Definition = namespace.getConstantDefinition(
            name = Symbol.of("contains5"),
        )!!

        assertEquals(
            expected = BoolValue(value = false),
            actual = contains5Definition.staticValue.value,
        )

        // Validate `mySet2`

        val mySet2Definition = namespace.getConstantDefinition(
            name = Symbol.of("mySet2"),
        )!!

        assertEquals(
            expected = SetValue(
                elements = setOf(
                    IntValue(value = 1L),
                    IntValue(value = 2L),
                    IntValue(value = 3L),
                    IntValue(value = 4L),
                ),
            ),
            actual = mySet2Definition.staticValue.value,
        )
    }
}

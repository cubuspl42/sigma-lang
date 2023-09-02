package tests

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Namespace
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Prelude
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class BuiltinsTests {
    @Test
    fun testSet() {
        val term = NamespaceDefinitionSourceTerm.parse(
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

        val mySet1Definition = namespace.getEntry(
            name = Symbol.of("mySet1"),
        )!!

        assertEquals(
            expected = SetType(
                elementType = IntCollectiveType,
            ),
            actual = mySet1Definition.effectiveType.value,
        )

        assertEquals(
            expected = SetValue(
                elements = setOf(
                    IntValue(value = 1L),
                    IntValue(value = 2L),
                    IntValue(value = 3L),
                ),
            ),
            actual = mySet1Definition.valueThunk.value,
        )

        // Validate `contains2`

        val contains2Definition = namespace.getEntry(
            name = Symbol.of("contains2"),
        )!!

        assertEquals(
            expected = BoolValue(value = true),
            actual = contains2Definition.valueThunk.value,
        )

        // Validate `contains5`

        val contains5Definition = namespace.getEntry(
            name = Symbol.of("contains5"),
        )!!

        assertEquals(
            expected = BoolValue(value = false),
            actual = contains5Definition.valueThunk.value,
        )

        // Validate `mySet2`

        val mySet2Definition = namespace.getEntry(
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
            actual = mySet2Definition.valueThunk.value,
        )
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.resolveName
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

        val namespaceBuildOutput = NamespaceDefinitionTerm.analyze(
            context = Expression.BuildContext.Builtin,
            qualifiedPath = QualifiedPath.Root,
            term = term,
        )

        val definitionBlock = namespaceBuildOutput.definitionBlock
        val namespaceBody = namespaceBuildOutput.namespaceBody

        assertEquals(
            expected = emptySet(),
            actual = namespaceBody.errors,
        )

        // Validate `mySet1`

        val mySet1Definition = definitionBlock.resolveName(
            name = Identifier.of("mySet1"),
        ) as ResolvedDefinition

        assertEquals(
            expected = SetType(
                elementType = IntCollectiveType,
            ),
            actual = mySet1Definition.body.inferredTypeOrIllType.getOrCompute(),
        )

        assertEquals(
            expected = SetValue(
                elements = setOf(
                    IntValue(value = 1L),
                    IntValue(value = 2L),
                    IntValue(value = 3L),
                ),
            ),
            actual = mySet1Definition.body.constClassified?.value,
        )

        // Validate `contains2`

        val contains2Definition = definitionBlock.resolveName(
            name = Identifier.of("contains2"),
        ) as ResolvedDefinition

        assertEquals(
            expected = BoolValue(value = true),
            actual = contains2Definition.body.constClassified?.value,
        )

        // Validate `contains5`
        val contains5Definition = definitionBlock.resolveName(
            name = Identifier.of("contains5"),
        ) as ResolvedDefinition

        assertEquals(
            expected = BoolValue(value = false),
            actual = contains5Definition.body.constClassified?.value,
        )

        // Validate `mySet2`
        val mySet2Definition = definitionBlock.resolveName(
            name = Identifier.of("mySet2"),
        ) as ResolvedDefinition

        assertEquals(
            expected = SetValue(
                elements = setOf(
                    IntValue(value = 1L),
                    IntValue(value = 2L),
                    IntValue(value = 3L),
                    IntValue(value = 4L),
                ),
            ),
            actual = mySet2Definition.body.constClassified?.value,
        )
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.imports

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResourceProjectStore
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportsTests {
    @Test
    fun test() {
        val loader = Project.Loader.create()

        val projectStore = ResourceProjectStore(
            javaClass = ImportsTests::class.java,
        )

        val project = loader.load(
            projectStore = projectStore,
        )

        val mainModule = project.mainModule
        val mainDefinition = project.entryPoint

        assertEquals(
            actual = mainModule.errors,
            expected = emptySet(),
        )

        assertEquals(
            expected = mainDefinition.computedBodyType.getOrCompute(),
            actual = IntCollectiveType,
        )

        assertEquals(
            expected = mainDefinition.valueThunk.value,
            actual = IntValue(value = 42L),
        )
    }
}

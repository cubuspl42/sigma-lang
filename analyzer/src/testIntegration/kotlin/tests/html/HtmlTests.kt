package tests.html

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResourceProjectStore
import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlTests {
    @Test
    fun test() {

        val projectStore = ResourceProjectStore(javaClass = HtmlTests::class.java)

        val project = Project.Loader.create().load(
            projectStore = projectStore,
        )

        assertEquals(
            expected = emptySet(),
            actual = project.errors,
        )

        println(project.entryPoint.valueThunk.value)
    }

}

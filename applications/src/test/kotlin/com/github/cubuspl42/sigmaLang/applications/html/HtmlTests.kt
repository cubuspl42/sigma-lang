package com.github.cubuspl42.sigmaLang.applications.html

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlTests {
    @Test
    fun test() {
        val project = loadHtmlProject()

        assertEquals(
            expected = emptySet(),
            actual = project.errors,
        )

        println(project.entryPoint.getValueThunk().value)
    }
}

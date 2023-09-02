package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Enclosed::class)
class ImportTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = ImportSourceTerm.parse(
                source = "%import utils".trimIndent()
            )

            assertEquals(
                expected = ImportSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    modulePath = ModulePath(name = "utils"),
                ),
                actual = term,
            )
        }
    }
}

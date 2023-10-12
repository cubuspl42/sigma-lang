package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ClassDefinitionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = """
                    %class Foo ^{
                        foo: Foo,
                        bar: Bar,
                    }
                """.trimIndent()
            )

            assertIs<ClassDefinitionSourceTerm>(term)

            assertEquals(
                expected = ClassDefinitionSourceTerm(
                    name = Identifier.of("Foo"),
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    body = UnorderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 11),
                        entries = listOf(
                            UnorderedTupleTypeConstructorSourceTerm.Entry(
                                name = Identifier.of("foo"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 2, columnIndex = 9),
                                    referredName = Identifier.of("Foo"),
                                ),
                            ),
                            UnorderedTupleTypeConstructorSourceTerm.Entry(
                                name = Identifier.of("bar"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 9),
                                    referredName = Identifier.of("Bar"),
                                ),
                            ),
                        ),
                    )
                ),
                actual = term,
            )
        }
    }
}

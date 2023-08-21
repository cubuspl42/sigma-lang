package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm
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
                    %class Foo (
                        %fields (
                            bar: Bar
                            id: Int
                        )

                        %method doSomething1 = ^{mArg1: Int, mArg2: Bool} => 42
                        
                        %method doSomething2 = ^[mArg1: Int] => 43
                    )
                """.trimIndent()
            )

            assertIs<ClassDefinitionSourceTerm>(term)

            val methodDefinition1 = ClassDefinitionSourceTerm.MethodDefinitionSourceTerm(
                name = Symbol.of("doSomething1"),
                location = SourceLocation(lineIndex = 7, columnIndex = 4),
                body = AbstractionSourceTerm(
                    location = SourceLocation(lineIndex = 7, columnIndex = 27),
                    argumentType = UnorderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 27),
                        entries = listOf(
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("mArg1"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 7, columnIndex = 36),
                                    referredName = Symbol.of("Int"),
                                ),
                            ),
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("mArg2"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 7, columnIndex = 48),
                                    referredName = Symbol.of("Bool"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 57),
                        value = IntValue(value = 42L),
                    ),
                ),
            )

            val methodDefinition2 = ClassDefinitionSourceTerm.MethodDefinitionSourceTerm(
                name = Symbol.of("doSomething2"),
                location = SourceLocation(lineIndex = 9, columnIndex = 4),
                body = AbstractionSourceTerm(
                    location = SourceLocation(lineIndex = 9, columnIndex = 27),
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 9, columnIndex = 27),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("mArg1"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 9, columnIndex = 36),
                                    referredName = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralSourceTerm(
                        location = SourceLocation(lineIndex = 9, columnIndex = 44),
                        value = IntValue(value = 43L),
                    ),
                ),
            )

            assertEquals(
                expected = ClassDefinitionSourceTerm(
                    name = Symbol.of("Foo"),
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    fieldDeclarations = listOf(
                        ClassDefinitionSourceTerm.FieldDeclarationSourceTerm(
                            location = SourceLocation(lineIndex = 3, columnIndex = 8),
                            name = Symbol.of("bar"),
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 3, columnIndex = 13),
                                referredName = Symbol.of("Bar"),
                            ),
                        ),
                        ClassDefinitionSourceTerm.FieldDeclarationSourceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 8),
                            name = Symbol.of("id"),
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 4, columnIndex = 12),
                                referredName = Symbol.of("Int"),
                            ),
                        ),
                    ),
                    methodDefinitions = listOf(
                        methodDefinition1,
                        methodDefinition2,
                    ),
                ),
                actual = term,
            )
        }
    }
}

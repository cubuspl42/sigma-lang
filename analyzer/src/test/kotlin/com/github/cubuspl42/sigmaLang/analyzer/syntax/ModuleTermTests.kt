package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val module = ModuleSourceTerm.parse(
                source = """
                    %const UserId = Int
                    
                    %const name1 = 123
                    
                    %const name2 = ^[a: Int] => 42
                    
                    %const name3 = {
                        a: 1,
                        b: 2,
                    }
                """.trimIndent()
            )

            assertEquals(
                expected = listOf(
                    ConstantDefinitionSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        name = Identifier.of("UserId"),
                        body = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 16),
                            referredName = Identifier.of("Int"),
                        )
                    ),
                    ConstantDefinitionSourceTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 0),
                        name = Identifier.of("name1"),
                        declaredTypeBody = null,
                        body = IntLiteralSourceTerm(
                            location = SourceLocation(lineIndex = 3, columnIndex = 15),
                            value = IntValue(value = 123L),
                        ),
                    ),
                    ConstantDefinitionSourceTerm(
                        location = SourceLocation(lineIndex = 5, columnIndex = 0),
                        name = Identifier.of("name2"), declaredTypeBody = null,
                        body = AbstractionConstructorSourceTerm(
                            location = SourceLocation(lineIndex = 5, columnIndex = 15),
                            argumentType = OrderedTupleTypeConstructorSourceTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 15),
                                elements = listOf(
                                    OrderedTupleTypeConstructorSourceTerm.Element(
                                        name = Identifier.of("a"),
                                        type = ReferenceSourceTerm(
                                            location = SourceLocation(lineIndex = 5, columnIndex = 20),
                                            referredName = Identifier.of("Int"),
                                        ),
                                    ),
                                ),
                            ),
                            image = IntLiteralSourceTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 28),
                                value = IntValue(value = 42L),
                            ),
                        ),
                    ),
                    ConstantDefinitionSourceTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 0),
                        name = Identifier.of("name3"), body = UnorderedTupleConstructorSourceTerm(
                            location = SourceLocation(lineIndex = 7, columnIndex = 15),
                            entries = listOf(
                                UnorderedTupleConstructorSourceTerm.Entry(
                                    name = Identifier.of("a"),
                                    value = IntLiteralSourceTerm(
                                        location = SourceLocation(lineIndex = 8, columnIndex = 7),
                                        value = IntValue(value = 1L),
                                    ),
                                ),
                                UnorderedTupleConstructorSourceTerm.Entry(
                                    name = Identifier.of("b"),
                                    value = IntLiteralSourceTerm(
                                        location = SourceLocation(lineIndex = 9, columnIndex = 7),
                                        value = IntValue(value = 2L),
                                    ),
                                ),
                            ),
                        )
                    ),
                ),
                actual = module.namespaceEntries,
            )
        }
    }
}

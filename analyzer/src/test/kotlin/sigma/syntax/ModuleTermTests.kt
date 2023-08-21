package sigma.syntax

import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val module = ModuleTerm.parse(
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
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        name = Symbol.of("UserId"),
                        body = ReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 16),
                            referee = Symbol.of("Int"),
                        )
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 0),
                        name = Symbol.of("name1"),
                        declaredTypeBody = null,
                        body = IntLiteralTerm(
                            location = SourceLocation(lineIndex = 3, columnIndex = 15),
                            value = IntValue(value = 123L),
                        ),
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 5, columnIndex = 0),
                        name = Symbol.of("name2"), declaredTypeBody = null,
                        body = AbstractionTerm(
                            location = SourceLocation(lineIndex = 5, columnIndex = 15),
                            argumentType = OrderedTupleTypeConstructorTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 15),
                                elements = listOf(
                                    OrderedTupleTypeConstructorTerm.Element(
                                        name = Symbol.of("a"),
                                        type = ReferenceTerm(
                                            location = SourceLocation(lineIndex = 5, columnIndex = 20),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                ),
                            ),
                            image = IntLiteralTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 28),
                                value = IntValue(value = 42L),
                            ),
                        ),
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 0),
                        name = Symbol.of("name3"), body = UnorderedTupleConstructorTerm(
                            location = SourceLocation(lineIndex = 7, columnIndex = 15),
                            entries = listOf(
                                UnorderedTupleConstructorTerm.Entry(
                                    name = Symbol.of("a"),
                                    value = IntLiteralTerm(
                                        location = SourceLocation(lineIndex = 8, columnIndex = 7),
                                        value = IntValue(value = 1L),
                                    ),
                                ),
                                UnorderedTupleConstructorTerm.Entry(
                                    name = Symbol.of("b"),
                                    value = IntLiteralTerm(
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

package sigma.syntax

import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.typeExpressions.OrderedTupleTypeConstructorTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val module = ModuleTerm.parse(
                source = """
                    typeAlias UserId = Int
                    
                    const name1 = 123
                    
                    const name2 = ^[a: Int] => 42
                    
                    const name3 = {
                        a: 1,
                        b: 2,
                    }
                """.trimIndent()
            )

            assertEquals(
                expected = listOf(
                    TypeAliasDefinitionTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        name = Symbol.of("UserId"),
                        definer = TypeReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 19),
                            referee = Symbol.of("Int"),
                        )
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 0),
                        name = Symbol.of("name1"),
                        type = null,
                        definer = IntLiteralTerm(
                            location = SourceLocation(lineIndex = 3, columnIndex = 14),
                            value = IntValue(value = 123L),
                        ),
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 5, columnIndex = 0),
                        name = Symbol.of("name2"), type = null,
                        definer = AbstractionTerm(
                            location = SourceLocation(lineIndex = 5, columnIndex = 14),
                            argumentType = OrderedTupleTypeConstructorTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 14),
                                elements = listOf(
                                    OrderedTupleTypeConstructorTerm.Element(
                                        name = Symbol.of("a"),
                                        type = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 5, columnIndex = 19),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                ),
                            ),
                            image = IntLiteralTerm(
                                location = SourceLocation(lineIndex = 5, columnIndex = 27),
                                value = IntValue(value = 42L),
                            ),
                        ),
                    ),
                    ConstantDefinitionTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 0),
                        name = Symbol.of("name3"), definer = UnorderedTupleConstructorTerm(
                            location = SourceLocation(lineIndex = 7, columnIndex = 14),
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
                actual = module.staticStatements,
            )
        }
    }
}

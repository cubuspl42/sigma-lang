package sigma.syntax

import sigma.TypeReferenceTerm
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteralBodyTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTests {
    object ParsingTests {
        @Test
        fun test() {
            val module = ModuleTerm.parse(
                source = """
                    import foo
                    import foo.bar.baz
                    import foo.bar
                    
                    name1 = 123
                    
                    name2 = [a: Int] => 42
                    
                    name3 = {
                        a: 1,
                        b: 2,
                    }
                """.trimIndent()
            )

            assertEquals(
                expected = listOf(
                    ModuleTerm.Import(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        path = listOf("foo"),
                    ),
                    ModuleTerm.Import(
                        location = SourceLocation(lineIndex = 2, columnIndex = 0),
                        path = listOf("foo", "bar", "baz"),
                    ),
                    ModuleTerm.Import(
                        location = SourceLocation(lineIndex = 3, columnIndex = 0),
                        path = listOf("foo", "bar"),
                    ),
                ),
                actual = module.imports,
            )

            assertEquals(
                expected = listOf(
                    DefinitionTerm(
                        location = SourceLocation(lineIndex = 5, columnIndex = 0),
                        name = Symbol.of("name1"),
                        valueType = null,
                        value = IntLiteralTerm(
                            location = SourceLocation(lineIndex = 5, columnIndex = 8),
                            value = IntValue(value = 123L),
                        ),
                    ),
                    DefinitionTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 0),
                        name = Symbol.of("name2"), valueType = null,
                        value = AbstractionTerm(
                            location = SourceLocation(lineIndex = 7, columnIndex = 8),
                            argumentBody = OrderedTupleTypeLiteralBodyTerm(
                                location = SourceLocation(lineIndex = 7, columnIndex = 8),
                                elements = listOf(
                                    OrderedTupleTypeLiteralBodyTerm.Element(
                                        name = Symbol.of("a"),
                                        type = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 7, columnIndex = 12),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                ),
                            ),
                            image = IntLiteralTerm(
                                location = SourceLocation(lineIndex = 7, columnIndex = 20),
                                value = IntValue(value = 42L),
                            ),
                        ),
                    ),
                    DefinitionTerm(
                        location = SourceLocation(lineIndex = 9, columnIndex = 0),
                        name = Symbol.of("name3"), value = UnorderedTupleLiteralTerm(
                            location = SourceLocation(lineIndex = 9, columnIndex = 8),
                            entries = listOf(
                                UnorderedTupleLiteralTerm.Entry(
                                    name = Symbol.of("a"),
                                    value = IntLiteralTerm(
                                        location = SourceLocation(lineIndex = 10, columnIndex = 7),
                                        value = IntValue(value = 1L),
                                    ),
                                ),
                                UnorderedTupleLiteralTerm.Entry(
                                    name = Symbol.of("b"),
                                    value = IntLiteralTerm(
                                        location = SourceLocation(lineIndex = 11, columnIndex = 7),
                                        value = IntValue(value = 2L),
                                    ),
                                ),
                            ),
                        )
                    ),
                ),
                actual = module.declarations,
            )
        }
    }
}

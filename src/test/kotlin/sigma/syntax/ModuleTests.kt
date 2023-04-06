package sigma.syntax

import sigma.TypeReference
import sigma.syntax.expressions.Abstraction
import sigma.syntax.expressions.IntLiteral
import sigma.syntax.expressions.UnorderedTupleLiteral
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteral
import sigma.values.IntValue
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleTests {
    object ParsingTests {
        @Test
        fun test() {
            val module = Module.parse(
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
                    Module.Import(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        path = listOf("foo"),
                    ),
                    Module.Import(
                        location = SourceLocation(lineIndex = 2, columnIndex = 0),
                        path = listOf("foo", "bar", "baz"),
                    ),
                    Module.Import(
                        location = SourceLocation(lineIndex = 3, columnIndex = 0),
                        path = listOf("foo", "bar"),
                    ),
                ),
                actual = module.imports,
            )

            assertEquals(
                expected = listOf(
                    Declaration(
                        location = SourceLocation(lineIndex = 5, columnIndex = 0),
                        name = Symbol.of("name1"),
                        valueType = null,
                        value = IntLiteral(
                            location = SourceLocation(lineIndex = 5, columnIndex = 8),
                            value = IntValue(value = 123L),
                        ),
                    ),
                    Declaration(
                        location = SourceLocation(lineIndex = 7, columnIndex = 0),
                        name = Symbol.of("name2"), valueType = null,
                        value = Abstraction(
                            location = SourceLocation(lineIndex = 7, columnIndex = 8),
                            argumentType = OrderedTupleTypeLiteral(
                                location = SourceLocation(lineIndex = 7, columnIndex = 8),
                                elements = listOf(
                                    OrderedTupleTypeLiteral.Element(
                                        name = Symbol.of("a"),
                                        type = TypeReference(
                                            location = SourceLocation(lineIndex = 7, columnIndex = 12),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                ),
                            ),
                            image = IntLiteral(
                                location = SourceLocation(lineIndex = 7, columnIndex = 20),
                                value = IntValue(value = 42L),
                            ),
                        ),
                    ),
                    Declaration(
                        location = SourceLocation(lineIndex = 9, columnIndex = 0),
                        name = Symbol.of("name3"), value = UnorderedTupleLiteral(
                            location = SourceLocation(lineIndex = 9, columnIndex = 8),
                            entries = listOf(
                                UnorderedTupleLiteral.Entry(
                                    name = Symbol.of("a"),
                                    value = IntLiteral(
                                        location = SourceLocation(lineIndex = 10, columnIndex = 7),
                                        value = IntValue(value = 1L),
                                    ),
                                ),
                                UnorderedTupleLiteral.Entry(
                                    name = Symbol.of("b"),
                                    value = IntLiteral(
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

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

class NamespaceDefinitionTermTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            val term = NamespaceDefinitionTerm.parse(
                source = """
                    namespace Foo (
                        typeAlias UserId = Int
                        
                        const name1 = 123
                        
                        const name2 = [a: Int] => 42
                        
                        const name3 = {
                            a: 1,
                            b: 2,
                        }
                    )
                """.trimIndent()
            )

            assertEquals(
                expected = NamespaceDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("Foo"),
                    staticStatements = listOf(
                        TypeAliasDefinitionTerm(
                            location = SourceLocation(lineIndex = 2, columnIndex = 4),
                            name = Symbol.of("UserId"),
                            definer = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 2, columnIndex = 23),
                                referee = Symbol.of("Int"),
                            )
                        ),
                        ConstantDefinitionTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 4),
                            name = Symbol.of("name1"),
                            type = null,
                            definer = IntLiteralTerm(
                                location = SourceLocation(lineIndex = 4, columnIndex = 18),
                                value = IntValue(value = 123L),
                            ),
                        ),
                        ConstantDefinitionTerm(
                            location = SourceLocation(lineIndex = 6, columnIndex = 4),
                            name = Symbol.of("name2"), type = null,
                            definer = AbstractionTerm(
                                location = SourceLocation(lineIndex = 6, columnIndex = 18),
                                argumentType = OrderedTupleTypeConstructorTerm(
                                    location = SourceLocation(lineIndex = 6, columnIndex = 18),
                                    elements = listOf(
                                        OrderedTupleTypeConstructorTerm.Element(
                                            name = Symbol.of("a"),
                                            type = TypeReferenceTerm(
                                                location = SourceLocation(lineIndex = 6, columnIndex = 22),
                                                referee = Symbol.of("Int"),
                                            ),
                                        ),
                                    ),
                                ),
                                image = IntLiteralTerm(
                                    location = SourceLocation(lineIndex = 6, columnIndex = 30),
                                    value = IntValue(value = 42L),
                                ),
                            ),
                        ),
                        ConstantDefinitionTerm(
                            location = SourceLocation(lineIndex = 8, columnIndex = 4),
                            name = Symbol.of("name3"), definer = UnorderedTupleConstructorTerm(
                                location = SourceLocation(lineIndex = 8, columnIndex = 18),
                                entries = listOf(
                                    UnorderedTupleConstructorTerm.Entry(
                                        name = Symbol.of("a"),
                                        value = IntLiteralTerm(
                                            location = SourceLocation(lineIndex = 9, columnIndex = 11),
                                            value = IntValue(value = 1L),
                                        ),
                                    ),
                                    UnorderedTupleConstructorTerm.Entry(
                                        name = Symbol.of("b"),
                                        value = IntLiteralTerm(
                                            location = SourceLocation(lineIndex = 10, columnIndex = 11),
                                            value = IntValue(value = 2L),
                                        ),
                                    ),
                                ),
                            )
                        ),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testNested() {
            val term = NamespaceDefinitionTerm.parse(
                source = """
                    namespace Foo (
                        namespace Bar (
                            const foo = 2
                        )
                    )
                """.trimIndent()
            )

            assertEquals(
                expected = NamespaceDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("Foo"),
                    staticStatements = listOf(
                        NamespaceDefinitionTerm(
                            location = SourceLocation(lineIndex = 2, columnIndex = 4),
                            name = Symbol.of("Bar"),
                            staticStatements = listOf(
                                ConstantDefinitionTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    name = Symbol.of("foo"),
                                    type = null,
                                    definer = IntLiteralTerm(
                                        location = SourceLocation(lineIndex = 3, columnIndex = 20),
                                        value = IntValue(value = 2L),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = term,
            )
        }
    }
}

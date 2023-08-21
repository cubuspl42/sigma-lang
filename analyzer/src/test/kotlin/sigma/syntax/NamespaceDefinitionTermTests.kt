package sigma.syntax

import sigma.syntax.expressions.ReferenceSourceTerm
import sigma.syntax.expressions.AbstractionSourceTerm
import sigma.syntax.expressions.IntLiteralSourceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorSourceTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class NamespaceDefinitionTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %const UserId = Int
                        
                        %const name1 = 123
                        
                        %const name2 = ^[a: Int] => 42
                        
                        %const name3 = {
                            a: 1,
                            b: 2,
                        }
                    )
                """.trimIndent()
            )

            assertEquals(
                expected = NamespaceDefinitionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("Foo"),
                    namespaceEntries = listOf(
                        ConstantDefinitionSourceTerm(
                            location = SourceLocation(lineIndex = 2, columnIndex = 4),
                            name = Symbol.of("UserId"),
                            body = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 2, columnIndex = 20),
                                referredName = Symbol.of("Int"),
                            )
                        ),
                        ConstantDefinitionSourceTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 4),
                            name = Symbol.of("name1"),
                            declaredTypeBody = null,
                            body = IntLiteralSourceTerm(
                                location = SourceLocation(lineIndex = 4, columnIndex = 19),
                                value = IntValue(value = 123L),
                            ),
                        ),
                        ConstantDefinitionSourceTerm(
                            location = SourceLocation(lineIndex = 6, columnIndex = 4),
                            name = Symbol.of("name2"), declaredTypeBody = null,
                            body = AbstractionSourceTerm(
                                location = SourceLocation(lineIndex = 6, columnIndex = 19),
                                argumentType = OrderedTupleTypeConstructorSourceTerm(
                                    location = SourceLocation(lineIndex = 6, columnIndex = 19),
                                    elements = listOf(
                                        OrderedTupleTypeConstructorSourceTerm.Element(
                                            name = Symbol.of("a"),
                                            type = ReferenceSourceTerm(
                                                location = SourceLocation(lineIndex = 6, columnIndex = 24),
                                                referredName = Symbol.of("Int"),
                                            ),
                                        ),
                                    ),
                                ),
                                image = IntLiteralSourceTerm(
                                    location = SourceLocation(lineIndex = 6, columnIndex = 32),
                                    value = IntValue(value = 42L),
                                ),
                            ),
                        ),
                        ConstantDefinitionSourceTerm(
                            location = SourceLocation(lineIndex = 8, columnIndex = 4),
                            name = Symbol.of("name3"), body = UnorderedTupleConstructorSourceTerm(
                                location = SourceLocation(lineIndex = 8, columnIndex = 19),
                                entries = listOf(
                                    UnorderedTupleConstructorSourceTerm.Entry(
                                        name = Symbol.of("a"),
                                        value = IntLiteralSourceTerm(
                                            location = SourceLocation(lineIndex = 9, columnIndex = 11),
                                            value = IntValue(value = 1L),
                                        ),
                                    ),
                                    UnorderedTupleConstructorSourceTerm.Entry(
                                        name = Symbol.of("b"),
                                        value = IntLiteralSourceTerm(
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
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %namespace Bar (
                            %const foo = 2
                        )
                    )
                """.trimIndent()
            )

            assertEquals(
                expected = NamespaceDefinitionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("Foo"),
                    namespaceEntries = listOf(
                        NamespaceDefinitionSourceTerm(
                            location = SourceLocation(lineIndex = 2, columnIndex = 4),
                            name = Symbol.of("Bar"),
                            namespaceEntries = listOf(
                                ConstantDefinitionSourceTerm(
                                    location = SourceLocation(lineIndex = 3, columnIndex = 8),
                                    name = Symbol.of("foo"),
                                    declaredTypeBody = null,
                                    body = IntLiteralSourceTerm(
                                        location = SourceLocation(lineIndex = 3, columnIndex = 21),
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

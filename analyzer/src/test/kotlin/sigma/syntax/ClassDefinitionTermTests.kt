package sigma.syntax

import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.IntLiteralTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ClassDefinitionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = NamespaceEntryTerm.parse(
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

            assertIs<ClassDefinitionTerm>(term)

            val methodDefinition1 = ClassDefinitionTerm.MethodDefinitionTerm(
                name = Symbol.of("doSomething1"),
                location = SourceLocation(lineIndex = 7, columnIndex = 4),
                body = AbstractionTerm(
                    location = SourceLocation(lineIndex = 7, columnIndex = 27),
                    argumentType = UnorderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 27),
                        entries = listOf(
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of("mArg1"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 7, columnIndex = 36),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of("mArg2"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 7, columnIndex = 48),
                                    referee = Symbol.of("Bool"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        location = SourceLocation(lineIndex = 7, columnIndex = 57),
                        value = IntValue(value = 42L),
                    ),
                ),
            )

            val methodDefinition2 = ClassDefinitionTerm.MethodDefinitionTerm(
                name = Symbol.of("doSomething2"),
                location = SourceLocation(lineIndex = 9, columnIndex = 4),
                body = AbstractionTerm(
                    location = SourceLocation(lineIndex = 9, columnIndex = 27),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 9, columnIndex = 27),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("mArg1"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 9, columnIndex = 36),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                    ),
                    image = IntLiteralTerm(
                        location = SourceLocation(lineIndex = 9, columnIndex = 44),
                        value = IntValue(value = 43L),
                    ),
                ),
            )

            assertEquals(
                expected = ClassDefinitionTerm(
                    name = Symbol.of("Foo"),
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    fieldDeclarations = listOf(
                        ClassDefinitionTerm.FieldDeclarationTerm(
                            location = SourceLocation(lineIndex = 3, columnIndex = 8),
                            name = Symbol.of("bar"),
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 3, columnIndex = 13),
                                referee = Symbol.of("Bar"),
                            ),
                        ),
                        ClassDefinitionTerm.FieldDeclarationTerm(
                            location = SourceLocation(lineIndex = 4, columnIndex = 8),
                            name = Symbol.of("id"),
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 4, columnIndex = 12),
                                referee = Symbol.of("Int"),
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
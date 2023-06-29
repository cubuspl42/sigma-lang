package sigma.syntax.type_expressions

import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import sigma.syntax.typeExpressions.TypeCallTerm
import sigma.syntax.typeExpressions.UnorderedTupleTypeConstructorTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeCallTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = TypeExpressionTerm.parse(
                source = "Foo[Bar, {a: Int, b: Bool}]",
            )

            assertEquals(
                expected = TypeCallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    callee = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("Foo"),
                    ),
                    passedArgument = TypeCallTerm.TypeTupleConstructor(
                        elements = listOf(
                            TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("Bar"),
                            ),
                            UnorderedTupleTypeConstructorTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                entries = listOf(
                                    UnorderedTupleTypeConstructorTerm.Entry(
                                        name = Symbol.of("a"),
                                        valueType = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                            referee = Symbol.of("Int"),
                                        ),
                                    ),
                                    UnorderedTupleTypeConstructorTerm.Entry(
                                        name = Symbol.of("b"),
                                        valueType = TypeReferenceTerm(
                                            location = SourceLocation(lineIndex = 1, columnIndex = 21),
                                            referee = Symbol.of("Bool"),
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
    }

    object EvaluationTests {
        @Test
        fun test() {
            val term = TypeExpressionTerm.parse(
                source = "Foo[Bar]",
            )
        }
    }
}

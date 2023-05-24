package sigma.syntax.type_expressions

import sigma.BuiltinTypeScope
import sigma.TypeReferenceTerm
import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.TupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.FunctionTypeTerm
import sigma.syntax.typeExpressions.TupleTypeLiteralTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeDepictionTests {
    object ParsingTests {
        @Test
        fun test() {
            val typeExpression = TypeExpressionTerm.parse(
                source = "{(a: A, b: B)} -> C",
            )

            assertEquals(
                expected = FunctionTypeTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = TupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        orderedEntries = listOf(
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("b"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                        unorderedEntries = emptyList(),
                    ),
                    imageType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 18),
                        referee = Symbol.of("C"),
                    ),
                ),
                actual = typeExpression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val type = TypeExpressionTerm.parse(
                source = "{(a: Int, b: Bool)} -> Bool",
            ).evaluate(
                typeScope = BuiltinTypeScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = TupleType(
                        orderedEntries = listOf(
                            TupleType.OrderedEntry(
                                index = 0,
                                name = Symbol.of("a"),
                                type = IntCollectiveType,
                            ),
                            TupleType.OrderedEntry(
                                index = 1,
                                name = Symbol.of("b"),
                                type = BoolType,
                            ),
                        ),
                        unorderedEntries = emptySet(),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

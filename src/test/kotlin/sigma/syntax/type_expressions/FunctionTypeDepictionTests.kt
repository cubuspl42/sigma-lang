package sigma.syntax.type_expressions

import sigma.BuiltinTypeScope
import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.metaExpressions.FunctionTypeTerm
import sigma.syntax.metaExpressions.OrderedTupleTypeLiteralTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeDepictionTests {
    object ParsingTests {
        @Test
        fun test() {
            val typeExpression = MetaExpressionTerm.parse(
                source = "[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeLiteralTerm.Element(
                                name = Symbol.of("a"),
                                type = MetaReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeLiteralTerm.Element(
                                name = Symbol.of("b"),
                                type = MetaReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = MetaReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 16),
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
            val type = MetaExpressionTerm.parse(
                source = "[a: Int, b: Bool] -> Bool",
            ).evaluate(
                typeScope = BuiltinTypeScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("a"),
                                type = IntCollectiveType,
                            ),
                            OrderedTupleType.Element(
                                name = Symbol.of("b"),
                                type = BoolType,
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

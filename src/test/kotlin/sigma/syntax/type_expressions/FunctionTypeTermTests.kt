package sigma.syntax.type_expressions

import sigma.semantics.BuiltinTypeScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.FunctionTypeTerm
import sigma.syntax.typeExpressions.OrderedTupleTypeConstructorTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val typeExpression = TypeExpressionTerm.parse(
                source = "[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("b"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = TypeReferenceTerm(
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
            val type = TypeExpressionTerm.parse(
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

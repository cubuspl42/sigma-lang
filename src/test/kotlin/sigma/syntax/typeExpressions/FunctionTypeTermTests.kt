package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinScope
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val typeExpression = TypeExpressionTerm.parse(
                source = "^[a: A, b: B] -> C",
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
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("b"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 17),
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
                source = "^[a: Int, b: Bool] -> Bool",
            ).evaluate(
                declarationScope = BuiltinScope,
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

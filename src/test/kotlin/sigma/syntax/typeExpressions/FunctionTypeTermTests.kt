package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FunctionTypeConstructorTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val typeExpression = ExpressionTerm.parse(
                source = "^[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = null,
                    argumentType = OrderedTupleTypeConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("a"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeConstructorTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 17),
                        referee = Symbol.of("C"),
                    ),
                ),
                actual = typeExpression,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {
//            val type = ExpressionTerm.parse(
//                source = "^[a: Int, b: Bool] -> Bool",
//            ).evaluate(
//                declarationScope = BuiltinScope,
//            )
//
//            assertEquals(
//                expected = UniversalFunctionType(
//                    argumentType = OrderedTupleType(
//                        elements = listOf(
//                            OrderedTupleType.Element(
//                                name = Symbol.of("a"),
//                                type = IntCollectiveType,
//                            ),
//                            OrderedTupleType.Element(
//                                name = Symbol.of("b"),
//                                type = BoolType,
//                            ),
//                        ),
//                    ),
//                    imageType = BoolType,
//                ),
//                actual = type,
//            )
        }
    }
}

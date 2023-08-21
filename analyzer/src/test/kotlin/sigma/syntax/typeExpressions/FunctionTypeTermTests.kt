package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.FunctionTypeConstructorSourceTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import sigma.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val typeExpression = ExpressionSourceTerm.parse(
                source = "^[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = null,
                    argumentType = OrderedTupleTypeConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        elements = listOf(
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referredName = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Symbol.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                    referredName = Symbol.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 17),
                        referredName = Symbol.of("C"),
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

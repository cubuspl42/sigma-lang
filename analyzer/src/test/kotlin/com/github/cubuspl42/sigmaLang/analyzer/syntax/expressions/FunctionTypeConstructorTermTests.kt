package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val typeExpression = ExpressionSourceTerm.parse(
                source = "^[a: A, b: B] -> C",
            )

            assertEquals(
                expected = FunctionTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    metaArgumentType = null,
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

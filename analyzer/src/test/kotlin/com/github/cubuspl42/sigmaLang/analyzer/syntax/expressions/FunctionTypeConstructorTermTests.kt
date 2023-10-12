package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
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
                                name = Identifier.of("a"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referredName = Identifier.of("A"),
                                ),
                            ),
                            OrderedTupleTypeConstructorSourceTerm.Element(
                                name = Identifier.of("b"),
                                type = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                    referredName = Identifier.of("B"),
                                ),
                            ),
                        ),
                    ),
                    imageType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 17),
                        referredName = Identifier.of("C"),
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

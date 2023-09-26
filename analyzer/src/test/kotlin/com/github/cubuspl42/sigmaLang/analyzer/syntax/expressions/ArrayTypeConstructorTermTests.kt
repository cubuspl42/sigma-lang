package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val expression = ExpressionSourceTerm.parse(
                source = "^[A...]",
            )

            assertEquals(
                expected = ArrayTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referredName = Symbol.of("A"),
                    ),
                ),
                actual = expression,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {
//            val type = ExpressionTerm.parse(
//                source = "^[A*]",
//            ).evaluate(
//                declarationScope = FakeDeclarationBlock.of(
//                    FakeTypeEntityDefinition(
//                        name = Symbol.of("A"),
//                        definedTypeEntity = BoolType,
//                    ),
//                ),
//            )
//
//            assertEquals(
//                expected = ArrayType(
//                    elementType = BoolType,
//                ),
//                actual = type,
//            )
        }
    }
}

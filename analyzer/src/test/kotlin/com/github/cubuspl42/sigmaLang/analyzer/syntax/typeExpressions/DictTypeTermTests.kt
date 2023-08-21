package com.github.cubuspl42.sigmaLang.analyzer.syntax.typeExpressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val expression = ExpressionSourceTerm.parse(
                source = "^{[K]: V}",
            )

            assertEquals(
                expected = DictTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    keyType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        referredName = Symbol.of("K"),
                    ),
                    valueType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 7),
                        referredName = Symbol.of("V"),
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
//                source = "^{[Int]: Bool}",
//            ).evaluate(
//                declarationScope = BuiltinScope,
//            )
//
//            assertEquals(
//                expected = DictType(
//                    keyType = IntCollectiveType,
//                    valueType = BoolType,
//                ),
//                actual = type,
//            )
        }
    }
}

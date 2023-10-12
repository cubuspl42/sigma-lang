package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeConstructorTermTests {
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
                        referredName = Identifier.of("K"),
                    ),
                    valueType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 7),
                        referredName = Identifier.of("V"),
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

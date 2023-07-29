package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.DictTypeConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = ExpressionTerm.parse(
                source = "^{[K]: V}",
            )

            assertEquals(
                expected = DictTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    keyType = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        referee = Symbol.of("K"),
                    ),
                    valueType = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 7),
                        referee = Symbol.of("V"),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
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

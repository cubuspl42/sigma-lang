package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.DictTypeConstructorSourceTerm
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.ReferenceSourceTerm
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
                        referee = Symbol.of("K"),
                    ),
                    valueType = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 7),
                        referee = Symbol.of("V"),
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

package sigma.syntax.typeExpressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ArrayTypeConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val expression = ExpressionTerm.parse(
                source = "^[A*]",
            )

            assertEquals(
                expected = ArrayTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referee = Symbol.of("A"),
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

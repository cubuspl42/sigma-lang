package sigma.syntax.typeExpressions

import sigma.evaluation.values.Symbol
import sigma.semantics.types.ArrayType
import sigma.semantics.types.BoolType
import sigma.syntax.SourceLocation
import utils.FakeDeclarationBlock
import utils.FakeTypeEntityDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpressionTerm.parse(
                source = "^[A*]",
            )

            assertEquals(
                expected = ArrayTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referee = Symbol.of("A"),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val type = TypeExpressionTerm.parse(
                source = "^[A*]",
            ).evaluate(
                declarationScope = FakeDeclarationBlock.of(
                    FakeTypeEntityDefinition(
                        name = Symbol.of("A"),
                        definedTypeEntity = BoolType,
                    ),
                ),
            )

            assertEquals(
                expected = ArrayType(
                    elementType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

package sigma.syntax.type_expressions

import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.ArrayTypeLiteralTerm
import sigma.semantics.types.ArrayType
import sigma.semantics.types.BoolType
import sigma.values.FixedTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpressionTerm.parse(
                source = "[A*]",
            )

            assertEquals(
                expected = ArrayTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
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
                source = "[A*]",
            ).evaluate(
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
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

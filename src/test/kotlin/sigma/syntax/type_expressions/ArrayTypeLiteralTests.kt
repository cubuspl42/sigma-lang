package sigma.syntax.type_expressions

import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.metaExpressions.ArrayTypeLiteralTerm
import sigma.semantics.types.ArrayType
import sigma.semantics.types.BoolType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = MetaExpressionTerm.parse(
                source = "[A*]",
            )

            assertEquals(
                expected = ArrayTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elementType = MetaReferenceTerm(
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
            val type = MetaExpressionTerm.parse(
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

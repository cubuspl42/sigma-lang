package sigma.syntax.type_expressions

import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeReferenceTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpressionTerm.parse(
                source = "Foo",
            )

            assertEquals(
                expected = TypeReferenceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    referee = Symbol.of("Foo"),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            val type = TypeExpressionTerm.parse(
                source = "Foo",
            ).evaluate(
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("Foo") to BoolType,
                    ),
                ),
            )

            assertEquals(
                expected = BoolType,
                actual = type,
            )
        }
    }
}
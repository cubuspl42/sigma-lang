package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IntCollectiveType
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinScope
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = TypeExpressionTerm.parse(
                source = "{[K]: V}",
            )

            assertEquals(
                expected = DictTypeTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    keyType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referee = Symbol.of("K"),
                    ),
                    valueType = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 6),
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
            val type = TypeExpressionTerm.parse(
                source = "{[Int]: Bool}",
            ).evaluate(
                declarationScope = BuiltinScope,
            )

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = type,
            )
        }
    }
}

package sigma.syntax.type_expressions

import sigma.BuiltinTypeScope
import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.metaExpressions.DictTypeTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IntCollectiveType
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class DictTypeDepictionTests {
    object ParsingTests {
        @Test
        fun test() {
            val expression = MetaExpressionTerm.parse(
                source = "{[K]: V}",
            )

            assertEquals(
                expected = DictTypeTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    keyType = MetaReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 2),
                        referee = Symbol.of("K"),
                    ),
                    valueType = MetaReferenceTerm(
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
            val type = MetaExpressionTerm.parse(
                source = "{[Int]: Bool}",
            ).evaluate(
                typeScope = BuiltinTypeScope,
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

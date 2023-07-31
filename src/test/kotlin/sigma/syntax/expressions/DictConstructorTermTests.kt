package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class DictConstructorTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        DictConstructorTerm.Association(
                            key = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referee = Symbol.of("foo"),
                            ),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referee = Symbol.of("value1"),
                            ),
                        ),
                        DictConstructorTerm.Association(
                            key = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz"),
                            ),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 23),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{[foo]: value1, [baz]: value2}"),
            )
        }
    }
}

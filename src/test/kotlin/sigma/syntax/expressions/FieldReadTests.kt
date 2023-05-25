package sigma.syntax.expressions

import org.junit.jupiter.api.assertThrows
import sigma.BuiltinTypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.FixedScope
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldReadTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = ExpressionTerm.parse("foo.bar"),
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testSimple() {
            val foo = DictTable(
                entries = mapOf(
                    Symbol.of("bar") to IntValue(value = 123L),
                ),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = ExpressionTerm.parse("foo.bar").evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("foo") to foo,
                        )
                    ),
                ),
            )
        }
    }

    object TypeCheckingTests {
        private val subjectType = UnorderedTupleType(
            valueTypeByName = mapOf(
                Symbol.of("bar") to IntCollectiveType,
            ),
        )

        private val valueScope = FixedStaticValueScope(
            entries = mapOf(
                Symbol.of("foo") to subjectType,
            ),
        )

        @Test
        fun testSimple() {
            assertEquals(
                expected = IntCollectiveType,
                actual = ExpressionTerm.parse("foo.bar").determineType(
                    typeScope = BuiltinTypeScope,
                    valueScope = valueScope,
                ),
            )
        }

        @Test
        fun testMissingKey() {
            assertThrows<TypeErrorException> {
                assertEquals(
                    expected = IntCollectiveType,
                    actual = ExpressionTerm.parse("foo.baz").determineType(
                        typeScope = BuiltinTypeScope,
                        valueScope = valueScope,
                    ),
                )
            }
        }

        @Test
        fun testBadSubject() {
            assertThrows<TypeErrorException> {
                assertEquals(
                    expected = IntCollectiveType,
                    actual = ExpressionTerm.parse("foo.baz").determineType(
                        typeScope = BuiltinTypeScope,
                        valueScope = FixedStaticValueScope(
                            entries = mapOf(
                                Symbol.of("foo") to UniversalFunctionType(
                                    argumentType = OrderedTupleType(
                                        elements = listOf(
                                            OrderedTupleType.Element(
                                                name = null,
                                                type = IntCollectiveType,
                                            ),
                                        ),
                                    ),
                                    imageType = BoolType,
                                ),
                            ),
                        ),
                    ),
                )
            }
        }
    }
}

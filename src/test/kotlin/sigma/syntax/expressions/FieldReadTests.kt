package sigma.syntax.expressions

import org.junit.jupiter.api.assertThrows
import sigma.BuiltinTypeScope
import sigma.StaticTypeScope
import sigma.Thunk
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.values.ComputableFunctionValue
import sigma.values.FixedStaticValueScope
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.Value
import sigma.values.tables.DictTable
import sigma.values.tables.FixedScope
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldRead(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = Expression.parse("foo.bar"),
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
                actual = Expression.parse("foo.bar").evaluate(
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
                actual = Expression.parse("foo.bar").validateAndInferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = valueScope,
                ),
            )
        }

        @Test
        fun testMissingKey() {
            assertThrows<TypeError> {
                assertEquals(
                    expected = IntCollectiveType,
                    actual = Expression.parse("foo.baz").validateAndInferType(
                        typeScope = BuiltinTypeScope,
                        valueScope = valueScope,
                    ),
                )
            }
        }

        @Test
        fun testBadSubject() {
            assertThrows<TypeError> {
                assertEquals(
                    expected = IntCollectiveType,
                    actual = Expression.parse("foo.baz").validateAndInferType(
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

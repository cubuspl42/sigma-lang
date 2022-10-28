package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object DictConstructorTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictConstructor(
                    content = TableConstructor(
                        entries = mapOf(
                            SymbolLiteral.of("foo") to Reference(Symbol.of("baz1")),
                            SymbolLiteral.of("bar") to Reference(Symbol.of("baz2")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, bar = baz2}"),
            )
        }

        @Test
        fun testWithArbitrary() {
            assertEquals(
                expected = DictConstructor(
                    content = TableConstructor(
                        entries = mapOf(
                            SymbolLiteral.of("foo") to Reference(Symbol.of("baz1")),
                            Reference(Symbol.of("baz")) to Reference(Symbol.of("baz2"))
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, [baz] = baz2}"),
            )
        }

        @Test
        fun testArray() {
            assertEquals(
                // TODO: Fix this, those int literals are artificial
                expected = DictConstructor(
                    content = TableConstructor(
                        entries = mapOf(
                            IntLiteral.of(0) to Reference(Symbol.of("foo")),
                            IntLiteral.of(1) to Reference(Symbol.of("bar")),
                            IntLiteral.of(2) to Reference(Symbol.of("baz")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo, bar, baz}"),
            )
        }
    }

    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = LetExpression(
                    declarations = listOf(
                        Declaration(
                            name = Symbol.of("g"),
                            value = Application(
                                subject = Reference(Symbol.of("h")),
                                argument = Reference(Symbol.of("a")),
                            ),
                        ),
                        Declaration(
                            name = Symbol.of("f"),
                            value = Reference(Symbol.of("g")),
                        ),
                    ),
                    result = Application(
                        subject = Reference(Symbol.of("f")),
                        argument = Reference(Symbol.of("x")),
                    ),
                ),
                actual = Expression.parse(
                    source = """
                        let {
                            g = h[a],
                            f = g,
                        } in f[x]
                    """.trimIndent()
                ),
            )
        }

        @Test
        fun testDeclarationWithTypeAnnotation() {
            assertEquals(
                expected = LetExpression(
                    declarations = listOf(
                        Declaration(
                            name = Symbol.of("a"),
                            valueType = TypeReference(
                                referee = Symbol.of("Int"),
                            ),
                            value = Reference(Symbol.of("b")),
                        ),
                    ),
                    result = Reference(Symbol.of("a")),
                ),
                actual = Expression.parse(
                    source = """
                        let { a: Int = b } in a
                    """.trimIndent()
                ),
            )
        }
    }

    object ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = Reference(Symbol("foo")),
                actual = Expression.parse("foo"),
            )
        }
    }

    object ApplicationTests {
        @Test
        fun testReferenceSubject() {
            assertEquals(
                expected = Application(
                    subject = Reference(Symbol("foo")),
                    argument = SymbolLiteral.of("bar"),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Application(
                    subject = DictConstructor(
                        content = TableConstructor(
                            entries = mapOf(
                                SymbolLiteral.of("foo") to SymbolLiteral.of("bar"),
                            ),
                        ),
                    ),
                    argument = SymbolLiteral.of("foo"),
                ),
                actual = Expression.parse(
                    source = "{foo = `bar`}[`foo`]",
                ),
            )
        }

        @Test
        fun testDictArgumentShorthand() {
            assertEquals(
                expected = Application(
                    subject = Reference(Symbol.of("foo")),
                    argument = DictConstructor.of(
                        entries = mapOf(
                            SymbolLiteral.of("bar") to SymbolLiteral.of("baz"),
                        ),
                    ),
                ),
                actual = Expression.parse(
                    source = "foo{bar = `baz`}",
                ),
            )
        }
    }
}

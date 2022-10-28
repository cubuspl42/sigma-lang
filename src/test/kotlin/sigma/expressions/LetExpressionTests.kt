package sigma.expressions

import sigma.TypeReference
import sigma.types.IntType
import sigma.types.SymbolType
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class LetExpressionTests {
    object ParsingTests {
        @Test
        fun testSimple() {
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
        fun testWithTypeAnnotation() {
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

    object TypeCheckingTests {
        @Test
        fun testSimple() {
            val type = Expression.parse(
                source = """
                    let {
                        a: Symbol = `foo`,
                    } in a
                """.trimIndent()
            ).obtainType()

            assertEquals(
                expected = SymbolType,
                actual = type,
            )
        }
    }
}

package sigma.syntax.expressions

import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.SyntaxValueScope
import sigma.TypeReferenceTerm
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.TupleType
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.AbstractionTerm.GenericParametersTuple
import sigma.syntax.typeExpressions.TupleTypeLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractionTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argumentType = TupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        orderedEntries = listOf(
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("n"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                        unorderedEntries = emptyList(),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 14),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "{(n: Int)} => 0",
                ),
            )
        }

        @Test
        fun testGenericWithMultipleParameters() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("a"),
                            Symbol.of("b"),
                        )
                    ),
                    argumentType = TupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        orderedEntries = listOf(
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                    referee = Symbol.of("a"),
                                ),
                            ),
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("b"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                    referee = Symbol.of("b"),
                                ),
                            ),
                        ),
                        unorderedEntries = emptyList(),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 26),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "![a, b] {(a: a, b: b)} => 0",
                ),
            )
        }

        @Test
        fun testGenericWithSingleParameter() {
            assertEquals(
                expected = AbstractionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    genericParametersTuple = GenericParametersTuple(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        parameterNames = listOf(
                            Symbol.of("t"),
                        )
                    ),
                    argumentType = TupleTypeLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 5),
                        orderedEntries = listOf(
                            TupleTypeLiteralTerm.Entry(
                                name = Symbol.of("n"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("Int"),
                                ),
                            ),
                        ),
                        unorderedEntries = emptyList(),
                    ),
                    image = IntLiteralTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 19),
                        value = IntValue(0),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "![t] {(n: Int)} => 0",
                ),
            )
        }
    }

    object TypeCheckingTests {
        @Test
        fun test() {
            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = TupleType.ordered(
                        TupleType.OrderedEntry(
                            index = 0,
                            name = Symbol.of("n"),
                            type = IntCollectiveType,
                        ),
                    ),
                    imageType = IntCollectiveType,
                ),
                actual = ExpressionTerm.parse(
                    source = "{(n: Int)} => n",
                ).determineType(
                    typeScope = BuiltinTypeScope,
                    valueScope = SyntaxValueScope.Empty,
                ),
            )
        }

        @Test
        fun testGenericSingleParameter() {
            val type = ExpressionTerm.parse(
                source = "![t] {(t: t)} => false",
            ).determineType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = TupleType.ordered(
                        TupleType.OrderedEntry(
                            index = 0,
                            name = Symbol.of("t"),
                            type = TypeVariable,
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }

        @Test
        fun testRecursiveCallTest() {
            val type = ExpressionTerm.parse(
                source = """
                    let {
                        f = {(n: Int)} -> Bool => f[n + 1]
                    } in f
                """.trimIndent(),
            ).determineType(
                typeScope = BuiltinTypeScope,
                valueScope = BuiltinScope,
            )

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = TupleType(
                        orderedEntries = listOf(
                            TupleType.OrderedEntry(
                                index = 0,
                                name = Symbol.of("n"),
                                type = IntCollectiveType,
                            ),
                        ),
                        unorderedEntries = emptySet(),
                    ),
                    imageType = BoolType,
                ),
                actual = type,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstraction = ExpressionTerm.parse(
                source = "{(n: Int, m: Int)} => n * m",
            ) as AbstractionTerm

            val closure = abstraction.evaluate(
                scope = BuiltinScope,
            )

            assertEquals(
                expected = IntValue(6),
                actual = closure.apply(
                    ArrayTable(
                        elements = listOf(
                            IntValue(2),
                            IntValue(3),
                        ),
                    ),
                ),
            )
        }
    }
}

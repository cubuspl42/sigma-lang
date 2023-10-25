@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test

class GenericConstructorTermTests {
    class ParsingTests {
        @Test
        fun testAbstractionBody1() {
            val term = ExpressionSourceTerm.parse(
                """
                    ^{
                        aType: Type,
                        bType: Type,
                    } !=> ^[
                        a: aType,
                        b: bType,
                    ] => {
                        x: a,
                        y: b,
                        z: 0,
                    }
                """.trimIndent()
            )

            assertMatches(
                matcher = GenericConstructorTermMatcher(
                    metaArgument = UnorderedTupleTypeConstructorTermMatcher(
                        entries = ListMatchers.inOrder(
                            UnorderedTupleTypeConstructorTermMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("aType")),
                                type = ReferenceTermMatcher(
                                    referredName = Matcher.Equals(Identifier.of("Type")),
                                ).checked(),
                            ),
                            UnorderedTupleTypeConstructorTermMatcher.EntryMatcher(
                                name = Matcher.Equals(Identifier.of("bType")),
                                type = ReferenceTermMatcher(
                                    referredName = Matcher.Equals(Identifier.of("Type")),
                                ).checked(),
                            ),
                        ),
                    ).checked(),
                    body = AbstractionConstructorTermMatcher(
                        argumentType = OrderedTupleTypeConstructorTermMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("a")),
                                    type = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("aType")),
                                    ).checked(),
                                ),
                                OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("b")),
                                    type = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("bType")),
                                    ).checked(),
                                ),
                            ),
                        ).checked(),
                        declaredImageType = Matcher.Equals(null),
                        image = UnorderedTupleConstructorTermMatcher(
                            entries = ListMatchers.inOrder(
                                UnorderedTupleConstructorTermMatcher.EntryMatcher(
                                    name = Matcher.Equals(Identifier.of("x")),
                                    value = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("a")),
                                    ).checked(),
                                ),
                                UnorderedTupleConstructorTermMatcher.EntryMatcher(
                                    name = Matcher.Equals(Identifier.of("y")),
                                    value = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("b")),
                                    ).checked(),
                                ),
                                UnorderedTupleConstructorTermMatcher.EntryMatcher(
                                    name = Matcher.Equals(Identifier.of("z")),
                                    value = IntLiteralTermMatcher(
                                        value = Matcher.Equals(IntValue.Zero),
                                    ).checked(),
                                ),
                            ),
                        ).checked(),
                    ).checked(),
                ).checked(),
                actual = term,
            )
        }

        @Test
        fun testAbstractionBody2() {
            val term = ExpressionSourceTerm.parse(
                source = "^[a: Type, b: Type] !=> ^[a: a, b: b] => 0",
            )

            assertMatches(
                matcher = GenericConstructorTermMatcher(
                    metaArgument = OrderedTupleTypeConstructorTermMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("a")),
                                type = ReferenceTermMatcher(
                                    referredName = Matcher.Equals(Identifier.of("Type")),
                                ).checked(),
                            ),
                            OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("b")),
                                type = ReferenceTermMatcher(
                                    referredName = Matcher.Equals(Identifier.of("Type")),
                                ).checked(),
                            ),
                        ),
                    ).checked(),
                    body = AbstractionConstructorTermMatcher(
                        argumentType = OrderedTupleTypeConstructorTermMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("a")),
                                    type = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("a")),
                                    ).checked(),
                                ),
                                OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("b")),
                                    type = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("b")),
                                    ).checked(),
                                ),
                            ),
                        ).checked(),
                        declaredImageType = Matcher.Equals(null),
                        image = IntLiteralTermMatcher(
                            value = Matcher.Equals(IntValue.Zero),
                        ).checked(),
                    ).checked(),
                ).checked(),
                actual = term,
            )
        }

        @Test
        fun testAbstractionBody3() {
            val term = ExpressionSourceTerm.parse(
                source = "^[t: Type] !=> ^[n: Int] => 0",
            )

            assertMatches(
                matcher = GenericConstructorTermMatcher(
                    metaArgument = OrderedTupleTypeConstructorTermMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("t")),
                                type = ReferenceTermMatcher(
                                    referredName = Matcher.Equals(Identifier.of("Type")),
                                ).checked(),
                            ),
                        ),
                    ).checked(),
                    body = AbstractionConstructorTermMatcher(
                        argumentType = OrderedTupleTypeConstructorTermMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeConstructorTermMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("n")),
                                    type = ReferenceTermMatcher(
                                        referredName = Matcher.Equals(Identifier.of("Int")),
                                    ).checked(),
                                ),
                            ),
                        ).checked(),
                        declaredImageType = Matcher.Equals(null),
                        image = IntLiteralTermMatcher(
                            value = Matcher.Equals(IntValue.Zero),
                        ).checked(),
                    ).checked(),
                ).checked(),
                actual = term,
            )
        }
    }
}

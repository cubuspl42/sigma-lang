@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.CallMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FieldReadMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.OrderedTupleConstructorMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ReferenceMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import utils.FakeStaticScope
import utils.FakeUserDeclaration
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("unused")
class CallTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = PostfixCallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Identifier.of("foo"),
                    ),
                    argument = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referredName = Identifier.of("bar"),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo(bar)"),
            )
        }

        @Test
        fun testFieldReadSubject() {
            val term = ExpressionSourceTerm.parse("foo.bar(baz)")

            assertEquals(
                expected = PostfixCallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = FieldReadSourceTerm(

                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        subject = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 0),
                            referredName = Identifier.of("foo"),
                        ),
                        fieldName = Identifier.of("bar"),
                    ),
                    argument = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        referredName = Identifier.of("baz"),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testUnorderedTupleArgumentSugar() {
            val term = ExpressionSourceTerm.parse("foo{arg1: value1, arg2: value2}")

            assertEquals(
                expected = PostfixCallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Identifier.of("foo"),
                    ),
                    argument = UnorderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Identifier.of("arg1"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referredName = Identifier.of("value1"),
                                ),
                            ),
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Identifier.of("arg2"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 24),
                                    referredName = Identifier.of("value2"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testOrderedTupleArgumentSugar() {
            val term = ExpressionSourceTerm.parse("foo[value1, value2]")

            assertEquals(
                expected = PostfixCallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Identifier.of("foo"),
                    ),
                    argument = OrderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        elements = listOf(
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referredName = Identifier.of("value1"),
                            ),
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                referredName = Identifier.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = term,
            )
        }

        @Test
        fun testMethodCall() {
            val term = ExpressionSourceTerm.parse("foo:bar[value1, value2]")

            assertMatches(
                matcher = MethodCallTermMatcher(
                    self = ReferenceTermMatcher(
                        referredName = Matcher.Equals(Identifier.of("foo")),
                    ).checked(),
                    method = ReferenceTermMatcher(
                        referredName = Matcher.Equals(Identifier.of("bar")),
                    ),
                    argument = OrderedTupleConstructorTermMatcher(
                        elements = ListMatchers.inOrder(
                            ReferenceTermMatcher(
                                referredName = Matcher.Equals(Identifier.of("value1")),
                            ).checked(),
                            ReferenceTermMatcher(
                                referredName = Matcher.Equals(Identifier.of("value2")),
                            ).checked(),
                        ),
                    ).checked(),
                ).checked(),
                actual = term,
            )
        }
    }

    class BuildingTests {
        @Test
        fun testSimple() {
            val term = ExpressionSourceTerm.parse("instance:method1[value1, value2]") as MethodCallTerm

            val call = Call.build(
                context = Expression.BuildContext(
                    outerScope = FakeStaticScope(
                        introductions = setOf(
                            FakeUserDeclaration(
                                name = Identifier.of("instance"),
                                declaredType = StringType,
                            ),
                            FakeUserDeclaration(
                                name = Identifier.of("method1"),
                                declaredType = UniversalFunctionType(
                                    argumentType = StringType,
                                    imageType = UniversalFunctionType(
                                        argumentType = OrderedTupleType.of(
                                            IntCollectiveType,
                                            BoolType,
                                        ),
                                        imageType = IntCollectiveType,
                                    ),
                                ),
                            ),
                            FakeUserDeclaration(
                                name = Identifier.of("value1"),
                                declaredType = IntCollectiveType,
                            ),
                            FakeUserDeclaration(
                                name = Identifier.of("value2"),
                                declaredType = BoolType,
                            ),
                        ),
                    ),
                ),
                term = term,
            ).resolved

            fun unorderedArgumentReferenceMatcher(
                name: Identifier,
            ): Matcher<Expression> = FieldReadMatcher(
                subject = ReferenceMatcher(
                    referredDeclaration = Matcher.Irrelevant(),
                ).checked(),
                fieldName = Matcher.Equals(name),
            ).checked()

            val methodMatcher = CallMatcher(
                subject = unorderedArgumentReferenceMatcher(
                    name = Identifier.of("method1"),
                ),
                argument = OrderedTupleConstructorMatcher.withElementsInOrder(
                    unorderedArgumentReferenceMatcher(
                        name = Identifier.of("instance"),
                    ).checked(),
                ).checked(),
            )

            assertMatches(
                matcher = CallMatcher(
                    subject = methodMatcher.checked(),
                    argument = OrderedTupleConstructorMatcher(
                        elements = ListMatchers.inOrder(
                            unorderedArgumentReferenceMatcher(
                                name = Identifier.of("value1"),
                            ),
                            unorderedArgumentReferenceMatcher(
                                name = Identifier.of("value2"),
                            ),
                        ),
                    ).checked(),
                ),
                actual = call,
            )
        }
    }
}

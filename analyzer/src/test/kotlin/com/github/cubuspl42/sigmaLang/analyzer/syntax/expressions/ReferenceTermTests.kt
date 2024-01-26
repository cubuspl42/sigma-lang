@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedUnorderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ErrorExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FieldReadMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.IntLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ReferenceMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import utils.CollectionMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test

@Suppress("unused")
class ReferenceTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = ExpressionSourceTerm.parse("foo")

            assertMatches(
                matcher = ReferenceTermMatcher(
                    referredName = Matcher.Equals(Identifier.of("foo")),
                ).checked(),
                actual = term,
            )
        }
    }

    class BuildingTests {
        @Test
        fun testReferringArgumentDeclaration() {
            val term = ExpressionSourceTerm.parse("foo") as ReferenceTerm

            val argumentDeclaration = AbstractionConstructorTerm.ArgumentDeclaration(
                declaredType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("foo") to IntCollectiveType,
                    ),
                ),
            )

            val buildOutput = term.analyze(
                context = Expression.BuildContext(
                    outerScope = StaticBlock.Fixed(
                        resolvedNameByName = mapOf(
                            Identifier.of("foo") to LeveledResolvedIntroduction(
                                level = StaticScope.Level.Primary,
                                resolvedIntroduction = ResolvedUnorderedArgument(
                                    name = Identifier.of("foo"),
                                    argumentDeclaration = argumentDeclaration,
                                ),
                            ),
                        ),
                    ),
                ),
            )

            val errors = buildOutput.errors
            val expression = buildOutput.expression

            assertMatches(
                matcher = CollectionMatchers.isEmpty(),
                actual = errors,
            )

            assertMatches(
                matcher = FieldReadMatcher(
                    subject = ReferenceMatcher(
                        referredDeclaration = Matcher.Equals(argumentDeclaration),
                    ).checked(),
                    fieldName = Matcher.Equals(Identifier.of("foo")),
                ).checked(),
                actual = expression,
            )
        }

        @Test
        fun testReferringDefinition() {
            val term = ExpressionSourceTerm.parse("foo") as ReferenceTerm

            val referredExpression = UnorderedTupleConstructor(
                entries = setOf(
                    UnorderedTupleConstructor.Entry(
                        name = Identifier.of("bar"),
                        value = IntLiteral.of(42L),
                    ),
                ),
            )

            val buildOutput = term.analyze(
                context = Expression.BuildContext(
                    outerScope = StaticBlock.Fixed(
                        resolvedNameByName = mapOf(
                            Identifier.of("foo") to LeveledResolvedIntroduction(
                                level = StaticScope.Level.Primary,
                                resolvedIntroduction = ResolvedDefinition(
                                    body = referredExpression,
                                ),
                            ),
                        ),
                    ),
                ),
            )

            val errors = buildOutput.errors
            val expression = buildOutput.expression

            assertMatches(
                matcher = CollectionMatchers.isEmpty(),
                actual = errors,
            )

            assertMatches(
                matcher = Matcher.Equals(referredExpression),
                actual = expression,
            )
        }

        @Test
        fun testUnresolved() {
            val term = ExpressionSourceTerm.parse("foo") as ReferenceTerm

            val buildOutput = term.analyze(
                context = Expression.BuildContext(
                    outerScope = StaticScope.Empty,
                ),
            )

            val errors = buildOutput.errors
            val expression = buildOutput.expression

            assertMatches(
                matcher = CollectionMatchers.eachOnce(
                    Matcher.Is<ReferenceTerm.UnresolvedNameError>(),
                ),
                actual = errors,
            )

            assertMatches(
                matcher = Matcher.Is<ErrorExpression>(),
                actual = expression,
            )
        }
    }
}

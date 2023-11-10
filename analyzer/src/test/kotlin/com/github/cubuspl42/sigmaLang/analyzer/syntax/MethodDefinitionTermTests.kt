@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BinaryOperator
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScopeMatchers.LevelResolvedIntroductionMatcher.Companion.primaryDefinitionMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructorMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.CallMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FieldReadMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ReferenceMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTermMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTermMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallTermMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTermMatcher
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test

@Suppress("unused")
class MethodDefinitionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = "%def Foo:method1 ^[n: Int] => 0",
            )

            assertMatches(
                matcher = MethodDefinitionTermMatcher(
                    thisType = ReferenceTermMatcher(
                        referredName = Matcher.Equals(Identifier.of("Foo")),
                    ).checked(),
                    name = Matcher.Equals(Identifier.of("method1")),
                    body = AbstractionConstructorTermMatcher(
                        argumentType = Matcher.Is<OrderedTupleTypeConstructorTerm>(),
                        declaredImageType = Matcher.IsNull(),
                        image = Matcher.Is<IntLiteralTerm>(),
                    ),
                ).checked(),
                actual = term,
            )
        }

        @Test
        fun testComplexSelf() {
            val term = NamespaceEntrySourceTerm.parse(
                source = "%def Foo[Bar]:method1 ^[n: Int] => 0",
            )

            assertMatches(
                matcher = MethodDefinitionTermMatcher(
                    thisType = PostfixCallTermMatcher(
                        subject = ReferenceTermMatcher(
                            referredName = Matcher.Equals(Identifier.of("Foo")),
                        ).checked(),
                        argument = OrderedTupleConstructorTermMatcher.withElementsInOrder(
                            ReferenceTermMatcher(
                                referredName = Matcher.Equals(Identifier.of("Bar")),
                            ).checked(),
                        ).checked(),
                    ).checked(),
                    name = Matcher.Equals(Identifier.of("method1")),
                    body = AbstractionConstructorTermMatcher(
                        argumentType = Matcher.Is<OrderedTupleTypeConstructorTerm>(),
                        declaredImageType = Matcher.IsNull(),
                        image = Matcher.Is<IntLiteralTerm>(),
                    ),
                ).checked(),
                actual = term,
            )
        }
    }

    class ConstructionTests {
        @Test
        fun test() {
            val term = NamespaceEntrySourceTerm.parse(
                source = "%def Foo:method1 ^[n: Int] => this.magicNumber * n",
            ) as MethodDefinitionTerm

            val thisType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("magicNumber") to IntCollectiveType,
                ),
            )

            val leveledResolvedIntroduction = NamespaceEntryTerm.build(
                context = Expression.BuildContext(
                    outerScope = StaticBlock.Fixed(
                        resolvedNameByName = mapOf(
                            Identifier.of("Foo") to LeveledResolvedIntroduction.metaIntroduction(
                                ResolvedDefinition(
                                    body = AtomicExpression(
                                        type = TypeType,
                                        value = thisType.asValue,
                                    ),
                                ),
                            ),
                        ),
                    ).chainWith(BuiltinScope),
                ),
                qualifiedPath = QualifiedPath.of("foo"),
                term = term,
            )

            assertMatches(
                matcher = primaryDefinitionMatcher(
                    body = Matcher.Built { methodExtractorConstructor: AbstractionConstructor ->
                        AbstractionConstructorMatcher(
                            argumentType = OrderedTupleTypeMatcher.withElementsInOrder(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.Equals(Identifier.of("this")),
                                    type = Matcher.Equals(thisType),
                                ),
                            ).checked(),
                            declaredImageType = Matcher.IsNull(),
                            image = Matcher.Built { methodConstructor: AbstractionConstructor ->
                                AbstractionConstructorMatcher(
                                    argumentType = OrderedTupleTypeMatcher.withElementsInOrder(
                                        OrderedTupleTypeMatcher.ElementMatcher(
                                            name = Matcher.Equals(Identifier.of("n")),
                                            type = Matcher.Is<IntType>(),
                                        ),
                                    ).checked(),
                                    declaredImageType = Matcher.IsNull(),
                                    image = CallMatcher.infix(
                                        operator = BinaryOperator.multiplication,
                                        leftArgument = FieldReadMatcher(
                                            subject = ReferenceMatcher.orderedArgument(
                                                declaration = methodExtractorConstructor.argumentDeclaration,
                                                argumentName = Identifier.of("this"),
                                            ).checked(),
                                            fieldName = Matcher.Equals(Identifier.of("magicNumber")),
                                        ).checked(),
                                        rightArgument = ReferenceMatcher.orderedArgument(
                                            declaration = methodConstructor.argumentDeclaration,
                                            argumentName = Identifier.of("n"),
                                        ),
                                    ).checked(),
                                )
                            }.checked(),
                        )
                    }.checked(),
                ),
                actual = leveledResolvedIntroduction,
            )
        }
    }
}

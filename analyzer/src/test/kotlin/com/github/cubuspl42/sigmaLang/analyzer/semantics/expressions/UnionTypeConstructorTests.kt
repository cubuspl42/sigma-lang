@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.buildReferenceMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm
import utils.CollectionMatchers
import utils.FakeArgumentDeclarationBlock
import utils.FakeUserDeclaration
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test
import kotlin.test.assertIs

class UnionTypeConstructorTests {
    class ConstructionTests {
        @Test
        fun testBuildFlat() {
            val term = ExpressionSourceTerm.parse(
                source = "A | ^[B]",
            ) as UnionTypeConstructorTerm

            val staticBlock = FakeArgumentDeclarationBlock.of(
                FakeUserDeclaration(
                    name = Identifier.of("A"),
                    declaredType = TypeType,
                ),
                FakeUserDeclaration(
                    name = Identifier.of("B"),
                    declaredType = TypeType,
                ),
            )

            val aResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("A"),
            )!!

            val bResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("B"),
            )!!

            val unionTypeConstructor = UnionTypeConstructor.build(
                context = Expression.BuildContext(
                    outerScope = staticBlock,
                ),
                term = term,
            ).resolved

            assertMatches(
                matcher = UnionTypeConstructorMatcher(
                    types = CollectionMatchers.eachOnce(
                        aResolvedName.resolvedIntroduction.buildReferenceMatcher(),
                        OrderedTupleTypeConstructorMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeConstructorMatcher.ElementMatcher(
                                    name = Matcher.IsNull(),
                                    type = bResolvedName.resolvedIntroduction.buildReferenceMatcher(),
                                ),
                            ),
                        ).checked(),
                    ),
                ),
                actual = unionTypeConstructor,
            )
        }

        @Test
        fun testBuildNested() {
            val term = ExpressionSourceTerm.parse(
                source = "A | B | D | C",
            ) as UnionTypeConstructorTerm

            val staticBlock = FakeArgumentDeclarationBlock.of(
                FakeUserDeclaration(
                    name = Identifier.of("A"),
                    declaredType = TypeType,
                ),
                FakeUserDeclaration(
                    name = Identifier.of("B"),
                    declaredType = TypeType,
                ),
                FakeUserDeclaration(
                    name = Identifier.of("C"),
                    declaredType = TypeType,
                ),
                FakeUserDeclaration(
                    name = Identifier.of("D"),
                    declaredType = TypeType,
                ),
            )

            val aResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("A"),
            )!!.resolvedIntroduction

            val bResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("B"),
            )!!.resolvedIntroduction

            val cResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("C"),
            )!!.resolvedIntroduction

            val dResolvedName = staticBlock.resolveNameLocally(
                name = Identifier.of("D"),
            )!!.resolvedIntroduction

            val unionTypeConstructor = UnionTypeConstructor.build(
                context = Expression.BuildContext(
                    outerScope = staticBlock,
                ),
                term = term,
            ).resolved

            assertMatches(
                matcher = UnionTypeConstructorMatcher(
                    types = CollectionMatchers.eachOnce(
                        aResolvedName.buildReferenceMatcher(),
                        bResolvedName.buildReferenceMatcher(),
                        cResolvedName.buildReferenceMatcher(),
                        dResolvedName.buildReferenceMatcher(),
                    ),
                ),
                actual = unionTypeConstructor,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "Int | Bool | ^[Bool]",
            ) as UnionTypeConstructorTerm

            val unionTypeConstructor = UnionTypeConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val evaluatedTypeValue = assertIs<TypeValue<*>>(
                unionTypeConstructor.evaluateValue(
                    context = EvaluationContext.Initial,
                    dynamicScope = DynamicScope.Empty,
                )
            )

            val unionType = assertIs<UnionType>(evaluatedTypeValue.asType)

            assertMatches(
                matcher = UnionTypeMatcher(
                    memberTypes = CollectionMatchers.eachOnce(
                        Matcher.Is<IntCollectiveType>().checked(),
                        Matcher.Is<BoolType>().checked(),
                        OrderedTupleTypeMatcher(
                            elements = ListMatchers.inOrder(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.IsNull(),
                                    type = Matcher.Is<BoolType>(),
                                ),
                            )
                        ).checked()
                    ),
                ),
                actual = unionType,
            )
        }
    }
}

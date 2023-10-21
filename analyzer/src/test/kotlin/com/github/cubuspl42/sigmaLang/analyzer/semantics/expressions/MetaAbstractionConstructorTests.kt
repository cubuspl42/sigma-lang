@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypePlaceholder
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaAbstractionConstructorTests {
    class BuildingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "!^[t: Type] ^[a: t] -> Int => 11",
            ) as AbstractionConstructorSourceTerm

            val metaAbstractionConstructor = MetaAbstractionConstructor.build(
                context = Expression.BuildContext.Builtin,
                metaArgumentTypeTerm = term.metaArgumentType!!,
                term = term,
            ).value

            assertMatches(
                matcher = MetaAbstractionConstructorMatcher(
                    metaArgumentTypeConstructor = OrderedTupleTypeMatcher(
                        elements = listOf(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("t")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                    body = AbstractionConstructorMatcher(
                        argumentType = OrderedTupleTypeMatcher(
                            elements = listOf(
                                OrderedTupleTypeMatcher.ElementMatcher(
                                    name = Matcher.Equals(expected = Identifier.of("a")),
                                    type = Matcher.Is<TypeVariable>(),
                                ),
                            ),
                        ).checked(),
                        declaredImageType = Matcher.Is<IntCollectiveType>(),
                        image = IntLiteralMatcher(
                            value = Matcher.Equals(11L),
                        ).checked(),
                    ).checked(),
                ),
                actual = metaAbstractionConstructor,
            )
        }
    }

    class KindInferenceTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "!^[t: Type] ^[a: t] -> Int => 11",
            ) as AbstractionConstructorSourceTerm

            val metaAbstractionConstructor = MetaAbstractionConstructor.build(
                context = Expression.BuildContext.Builtin,
                metaArgumentTypeTerm = term.metaArgumentType!!,
                term = term,
            ).value

            assertMatches(
                matcher = KindConstructorMatcher(
                    metaArgumentType = OrderedTupleTypeMatcher(
                        elements = listOf(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(expected = Identifier.of("t")),
                                type = Matcher.Is<TypeType>(),
                            ),
                        ),
                    ).checked(),
                ).checked(),
                actual = metaAbstractionConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class ClassificationTests {
        @Test
        fun test() {

        }
    }

    class EvaluationTests {
        @Test
        fun test() {

        }
    }
}

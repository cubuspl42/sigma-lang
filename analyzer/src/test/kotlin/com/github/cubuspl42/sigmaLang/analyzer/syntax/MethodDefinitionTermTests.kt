@file:Suppress("JUnitMalformedDeclaration")

package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTermMatcher
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
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
                    self = ReferenceTermMatcher(
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
    }
}

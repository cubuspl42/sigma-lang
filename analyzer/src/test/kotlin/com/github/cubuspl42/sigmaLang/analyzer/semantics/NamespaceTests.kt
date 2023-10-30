package com.github.cubuspl42.sigmaLang.analyzer.semantics

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import utils.CollectionMatchers
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class NamespaceTests {
    class TypeCheckingTests {
        @Test
        fun testInnerTypeScope() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %meta UserId = Int
                        
                        %const isUserIdValid = ^[userId: UserId] => true
                    )
                """.trimIndent(),
            )

            val namespaceBuildOutput = NamespaceDefinition.build(
                context = Expression.BuildContext.Builtin,
                qualifiedPath = QualifiedPath.Root,
                term = term,
            )

            val definitionBlock = namespaceBuildOutput.definitionBlock

            val isUserIdValidDefinition = definitionBlock.resolveName(
                name = Identifier.of("isUserIdValid"),
            ) as ResolvedDefinition

            assertNotNull(isUserIdValidDefinition)

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("userId")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                        )
                    ).checked(),
                    imageType = Matcher.Is<BoolType>(),
                ).checked(),
                actual = isUserIdValidDefinition.body.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }
}

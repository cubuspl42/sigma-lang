package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class NamespaceTests {
    class TypeCheckingTests {
        @Test
        @Ignore // TODO: Re-support type aliases
        fun testInnerTypeScope() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %const UserId = Int
                        
                        %const isUserIdValid = ^[userId: UserId] => true
                    )
                """.trimIndent(),
            )

            val namespaceDefinition = NamespaceDefinition.build(
                context = Expression.BuildContext.Builtin,
                qualifiedPath = QualifiedPath.Root,
                term = term,
            )

            val isUserIdValidDefinition = namespaceDefinition.getDefinition(
                name = Identifier.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValidDefinition)

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Identifier.of("userId"),
                                type = IntCollectiveType,
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = isUserIdValidDefinition.computedBodyType.getOrCompute(),
            )
        }
    }
}

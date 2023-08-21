package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NamespaceTests {
    class TypeCheckingTests {
        @Test
        fun testInnerTypeScope() {
            val term = NamespaceDefinitionSourceTerm.parse(
                source = """
                    %namespace Foo (
                        %const UserId = Int
                        
                        %const isUserIdValid = ^[userId: UserId] => true
                    )
                """.trimIndent(),
            )

            val namespace = Namespace.build(
                prelude = Prelude.load(),
                term = term,
            )

            val isUserIdValidDefinition = namespace.getConstantDefinition(
                name = Symbol.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValidDefinition)

            assertEquals(
                expected = UniversalFunctionType(
                    argumentType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("userId"),
                                type = IntCollectiveType,
                            ),
                        ),
                    ),
                    imageType = BoolType,
                ),
                actual = isUserIdValidDefinition.asValueDefinition.effectiveValueType.value,
            )
        }
    }
}

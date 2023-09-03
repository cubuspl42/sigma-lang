package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModuleTests {
    class TypeCheckingTests {
        @Test
        fun testTypeScope() {
            val term = ModuleSourceTerm.parse(
                source = """
                    %const UserId = Int
                    
                    %const isUserIdValid = ^[userId: UserId] => true
                """.trimIndent(),
            )

            val module = Module.build(
                prelude = Prelude.load(),
                term = term,
            )

            val isUserIdValid = module.rootNamespaceDefinition.getDefinition(
                name = Symbol.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValid)

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
                actual = isUserIdValid.effectiveType.value,
            )
        }
    }
}

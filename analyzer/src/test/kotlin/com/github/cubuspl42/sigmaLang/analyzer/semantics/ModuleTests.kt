package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
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
                outerScope = BuiltinScope,
                moduleResolver = ModuleResolver.Empty,
                modulePath = ModulePath(name = "__module__"),
                term = term,
            )

            val isUserIdValid = module.rootNamespaceDefinition.getDefinition(
                name = Identifier.of("isUserIdValid"),
            )

            assertNotNull(isUserIdValid)

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
                actual = isUserIdValid.computedEffectiveType.getOrCompute(),
            )
        }

        @Test
        fun testImport() {
            val module1Term = ModuleSourceTerm.parse(
                source = """
                    %const bar: Int = 123
                """.trimIndent(),
            )

            val module2Term = ModuleSourceTerm.parse(
                source = """
                    %import foo
                    
                    %const baz = foo.bar
                """.trimIndent(),
            )

            val module = Module.build(
                outerScope = BuiltinScope,
                moduleResolver = object : ModuleResolver {
                    override fun resolveModule(
                        modulePath: ModulePath,
                    ): Module? = Module.build(
                        outerScope = BuiltinScope,
                        moduleResolver = this,
                        modulePath = modulePath,
                        term = module1Term,
                    ).takeIf {
                        modulePath.name == "foo"
                    }
                },
                modulePath = ModulePath(name = "__module__"),
                term = module2Term,
            )

            val bazDefinition = module.rootNamespaceDefinition.getDefinition(
                name = Identifier.of("baz"),
            )

            assertEquals(
                actual = module.errors,
                expected = emptySet(),
            )

            assertNotNull(bazDefinition)

            assertIs<ConstantDefinition>(bazDefinition)

            assertEquals(
                expected = bazDefinition.computedEffectiveType.getOrCompute(),
                actual = IntCollectiveType,
            )

            assertEquals(
                expected = bazDefinition.valueThunk.value,
                actual = IntValue(value = 123L),
            )
        }
    }
}

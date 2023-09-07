package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
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
                actual = isUserIdValid.effectiveTypeThunk.value,
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
                name = Symbol.of("baz"),
            )

            assertEquals(
                actual = module.errors,
                expected = emptySet(),
            )

            assertNotNull(bazDefinition)

            assertIs<ConstantDefinition>(bazDefinition)

            assertEquals(
                expected = bazDefinition.effectiveTypeThunk.value,
                actual = IntCollectiveType,
            )

            assertEquals(
                expected = bazDefinition.valueThunk.value,
                actual = IntValue(value = 123L),
            )
        }
    }
}

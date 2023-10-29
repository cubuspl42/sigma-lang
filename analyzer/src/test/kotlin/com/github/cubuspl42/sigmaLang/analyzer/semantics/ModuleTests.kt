package com.github.cubuspl42.sigmaLang.analyzer.semantics

import UniversalFunctionTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleTypeMatcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import utils.ListMatchers
import utils.Matcher
import utils.assertMatches
import utils.checked
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(Enclosed::class)
class ModuleTests {
    class TypeCheckingTests {
        @Test
        fun testTypeScope() {
            val term = ModuleSourceTerm.parse(
                source = """
                    %meta UserId = Int
                    
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

            assertMatches(
                matcher = UniversalFunctionTypeMatcher(
                    argumentType = OrderedTupleTypeMatcher(
                        elements = ListMatchers.inOrder(
                            OrderedTupleTypeMatcher.ElementMatcher(
                                name = Matcher.Equals(Identifier.of("userId")),
                                type = Matcher.Is<IntCollectiveType>(),
                            ),
                        ),
                    ).checked(),
                    imageType = Matcher.Is<BoolType>(),
                ).checked(),
                actual = isUserIdValid.computedBodyType.getOrCompute(),
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
                expected = emptySet(),
                actual = module.errors,
            )

            assertNotNull(bazDefinition)

            assertEquals(
                expected = IntCollectiveType,
                actual = bazDefinition.computedBodyType.getOrCompute(),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = bazDefinition.valueThunk.value,
            )
        }
    }
}

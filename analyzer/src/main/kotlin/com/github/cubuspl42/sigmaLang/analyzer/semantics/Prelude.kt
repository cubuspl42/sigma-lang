package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.getResourceAsText
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LocalScopeSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope

data class Prelude(
    val declarationScope: StaticScope,
    val dynamicScope: DynamicScope,
) {
    companion object {
        fun load(): Prelude {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            val prelude = LocalScopeSourceTerm.parse(
                sourceName = "prelude",
                source = preludeSource,
            )

            val (definitionBlock, declarationScope) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = VariableDefinitionBlock.build(
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = prelude.definitions,
                )

                return@looped Pair(
                    definitionBlock,
                    definitionBlock.chainWith(BuiltinScope),
                )
            }

            val preludeScope = definitionBlock.evaluate(
                dynamicScope = BuiltinScope,
            )

            return Prelude(
                declarationScope = declarationScope,
                dynamicScope = preludeScope,
            )
        }
    }
}

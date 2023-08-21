package sigma.semantics

import getResourceAsText
import sigma.syntax.expressions.LocalScopeSourceTerm
import sigma.evaluation.scope.Scope

data class Prelude(
    val declarationScope: StaticScope,
    val scope: Scope,
) {
    companion object {
        fun load(): Prelude {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            val prelude = LocalScopeSourceTerm.parse(
                sourceName = "prelude",
                source = preludeSource,
            )

            val (definitionBlock, declarationScope) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalValueDefinitionBlock.build(
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = prelude.definitions,
                )

                return@looped Pair(
                    definitionBlock,
                    definitionBlock.chainWith(BuiltinScope),
                )
            }

            val preludeScope = definitionBlock.evaluate(
                scope = BuiltinScope,
            )

            return Prelude(
                declarationScope = declarationScope,
                scope = preludeScope,
            )
        }
    }
}

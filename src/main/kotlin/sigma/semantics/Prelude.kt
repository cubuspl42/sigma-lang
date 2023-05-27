package sigma.semantics

import getResourceAsText
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.syntax.expressions.LocalScopeTerm
import sigma.evaluation.scope.Scope

data class Prelude(
    val definitionBlock: LocalDefinitionBlock,
    val scope: Scope,
) {
    companion object {
        fun load(): Prelude {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            val prelude = LocalScopeTerm.parse(
                sourceName = "prelude",
                source = preludeSource,
            )

            val (definitionBlock, _) = DeclarationScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalDefinitionBlock.build(
                    typeScope = BuiltinTypeScope,
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = prelude.definitions,
                )


                return@looped Pair(
                    definitionBlock,
                    definitionBlock,
                )
            }

            val preludeScope = prelude.evaluateDynamically(
                scope = BuiltinScope,
            )

            return Prelude(
                definitionBlock = definitionBlock,
                scope = preludeScope,
            )
        }
    }
}

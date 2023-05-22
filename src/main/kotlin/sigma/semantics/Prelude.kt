package sigma.semantics

import getResourceAsText
import sigma.BuiltinScope
import sigma.BuiltinTypeScope
import sigma.SyntaxValueScope
import sigma.syntax.expressions.LocalScopeTerm
import sigma.values.tables.Scope

data class Prelude(
    val definitionBlock: DefinitionBlock,
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
                val definitionBlock = DefinitionBlock.build(
                    typeScope = BuiltinTypeScope,
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = prelude.declarations,
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

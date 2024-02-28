package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm
import com.github.cubuspl42.sigmaLang.shell.terms.LetInTerm

class DefinitionScope(
    private val definitionBodyByName: Map<IdentifierTerm, Lazy<Expression>>,
) : Scope {
    companion object {
        fun construct(
            context: ConstructionContext,
            letInTerm: LetInTerm,
        ): DefinitionScope {
            val (definitionScope, _) = Scope.looped { innerScopeLooped ->
                val definitionBodyByName = letInTerm.block.entries.associate {
                    it.key to it.value.construct(
                        context = context.copy(
                            scope = innerScopeLooped,
                        ),
                    )
                }

                val letInScope = DefinitionScope(
                    definitionBodyByName = definitionBodyByName,
                )

                val innerScope = letInScope.chainWith(context.scope)

                Pair(
                    letInScope,
                    innerScope,
                )
            }

            return definitionScope
        }
    }

    override fun resolveName(referredName: IdentifierTerm): Scope.ReferenceResolution =
        definitionBodyByName[referredName]?.let {
            Scope.DefinitionReference(
                referredBodyLazy = it,
            )
        } ?: Scope.UnresolvedReference
}

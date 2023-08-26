package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.LoopedScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class LocalValueDefinitionBlock(
    private val declarationScope: StaticScope,
    private val declarations: List<LocalDefinitionTerm>,
) : StaticBlock() {
    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            definitions: List<LocalDefinitionTerm>,
        ): LocalValueDefinitionBlock = LocalValueDefinitionBlock(
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName by lazy {
        declarations.associate {
            it.name to LocalValueDefinition.build(
                declarationScope = declarationScope,
                term = it,
            )
        }
    }

    fun getValueDefinition(name: Symbol): LocalValueDefinition? = definitionByName[name]

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedName? = getValueDefinition(name = name)?.let {
        ResolvedName(
            type = it.effectiveValueType, resolution = DynamicResolution(
                resolvedFormula = null,
            )
        )
    }

    val subExpressions by lazy { definitionByName.values.map { it.body }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        scope: Scope,
    ): Scope = LoopedScope(
        outerScope = scope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.body
        },
    )
}

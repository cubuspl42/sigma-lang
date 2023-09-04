package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.LoopedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class VariableDefinitionBlock(
    private val declarationScope: StaticScope,
    private val declarations: List<LocalDefinitionTerm>,
) : StaticBlock() {
    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            definitions: List<LocalDefinitionTerm>,
        ): VariableDefinitionBlock = VariableDefinitionBlock(
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName by lazy {
        declarations.associate {
            it.name to UserVariableDefinition.build(
                declarationScope = declarationScope,
                term = it,
            )
        }
    }

    fun getValueDefinition(name: Symbol): UserVariableDefinition? = definitionByName[name]

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvableDeclaration? = getValueDefinition(name = name)

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys

    val subExpressions by lazy { definitionByName.values.map { it.body }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        dynamicScope: DynamicScope,
    ): DynamicScope = LoopedDynamicScope(
        outerDynamicScope = dynamicScope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.body
        },
    )
}

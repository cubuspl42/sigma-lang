package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.LoopedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class VariableDefinitionBlock(
    private val declarationScope: StaticScope,
    private val declarationTerms: List<LocalDefinitionTerm>,
) : StaticBlock() {
    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            definitions: List<LocalDefinitionTerm>,
        ): VariableDefinitionBlock = VariableDefinitionBlock(
            declarationScope = outerDeclarationScope,
            declarationTerms = definitions,
        )
    }

    val declarations = declarationTerms.map {
        UserVariableDefinition.build(
            declarationScope = declarationScope,
            term = it,
        )
    }.toSet()

    private val definitionByName by lazy {
        declarations.associateBy { it.name }
    }

    fun getValueDefinition(name: Symbol): UserVariableDefinition? = definitionByName[name]

    override fun resolveNameLocally(
        name: Symbol,
    ): Introduction? = getValueDefinition(name = name)

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys

    val subExpressions: Set<Expression> by lazy { definitionByName.values.map { it.assignedBody }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        outerScope: DynamicScope,
    ): DynamicScope = LoopedDynamicScope(
        outerDynamicScope = outerScope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.assignedBody
        },
    )
}

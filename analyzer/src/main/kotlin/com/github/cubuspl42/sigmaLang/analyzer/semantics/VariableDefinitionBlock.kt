package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.LoopedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class VariableDefinitionBlock(
    private val context: Expression.BuildContext,
    private val declarationTerms: List<LocalDefinitionTerm>,
) : StaticBlock() {
    companion object {
        fun build(
            context: Expression.BuildContext,
            definitions: List<LocalDefinitionTerm>,
        ): VariableDefinitionBlock = VariableDefinitionBlock(
            context = context,
            declarationTerms = definitions,
        )
    }

    val declarations = declarationTerms.map {
        UserVariableDefinition.build(
            context = context,
            term = it,
        )
    }.toSet()

    private val definitionByName by lazy {
        declarations.associateBy { it.name }
    }

    fun getValueDefinition(name: Symbol): UserVariableDefinition? = definitionByName[name]

    override fun resolveNameLocally(
        name: Symbol,
    ): ClassifiedIntroduction? = getValueDefinition(name = name)

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys

    val subExpressions by lazy { definitionByName.values.map { it.body }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun evaluate(
        outerScope: DynamicScope,
    ): DynamicScope = LoopedDynamicScope(
        outerDynamicScope = outerScope,
        expressionByName = definitionByName.mapValues { (_, definition) ->
            definition.body
        },
    )
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
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

    private val definitionByName = declarationTerms.associate {
        it.name to ResolvedDefinition(
            definition = UserVariableDefinition.build(
                context = context,
                term = it,
            ),
        )
    }

    fun getValueDefinition(name: Symbol): Definition? = resolveNameLocally(name)?.definition

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedDefinition? = definitionByName[name]

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys

//    val subExpressions by lazy { definitionByName.values.map { it. body }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.body.errors }
    }
}

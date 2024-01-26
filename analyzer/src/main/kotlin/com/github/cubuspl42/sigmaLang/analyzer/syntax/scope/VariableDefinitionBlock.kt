package com.github.cubuspl42.sigmaLang.analyzer.syntax.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.UserVariableDefinition
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
        it.name to LeveledResolvedIntroduction(
            level = StaticScope.Level.Primary,
            resolvedIntroduction = UserVariableDefinition.build(
                context = context,
                term = it,
            ),
        )
    }

    fun getValueDefinition(name: Symbol): ResolvedDefinition? {
        return resolveName(name) as ResolvedDefinition?
    }

    override fun resolveNameLocally(
        name: Symbol,
    ): LeveledResolvedIntroduction? = definitionByName[name]

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys

//    val subExpressions by lazy { definitionByName.values.map { it. body }.toSet() }

    val errors: Set<SemanticError> by lazy {
        definitionByName.values.fold(emptySet()) { acc, it -> acc + it.resolvedIntroduction.errors }
    }
}

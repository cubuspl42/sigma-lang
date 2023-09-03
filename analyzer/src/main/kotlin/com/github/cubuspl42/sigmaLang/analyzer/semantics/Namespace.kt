package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm

interface Namespace {
    companion object {
        fun build(
            prelude: Prelude,
            term: NamespaceDefinitionTerm,
        ): NamespaceImpl = NamespaceImpl(
            prelude = prelude,
            term = term,
        )
    }

    val entries: Set<NamespaceEntry>

    val asDeclarationBlock: NamespaceImpl.NamespaceStaticBlock

    val innerStaticScope: StaticScope

    val innerDynamicScope: DynamicScope

    val expressionMap: ExpressionMap

    val errors: Set<SemanticError>

    fun getEntry(
        name: Symbol,
    ): NamespaceEntry?

    fun printErrors()
}

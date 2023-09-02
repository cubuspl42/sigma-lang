package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm

class Namespace(
    private val prelude: Prelude,
    private val term: NamespaceDefinitionTerm,
) : Value() {
    companion object {
        fun build(
            prelude: Prelude,
            term: NamespaceDefinitionTerm,
        ): Namespace = Namespace(
            prelude = prelude,
            term = term,
        )
    }

    private val entries: Set<NamespaceEntry> = term.namespaceEntries.map {
        NamespaceEntry.build(
            containingNamespace = this,
            term = it,
        )
    }.toSet()

    fun getEntry(
        name: Symbol,
    ): NamespaceEntry? = entries.singleOrNull {
        it.name == name
    }

    inner class NamespaceStaticBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = getEntry(name = name)?.let {
            ResolvedName(
                type = it.effectiveType,
                resolution = StaticResolution(
                    namespaceEntry = it,
                ),
            )
        }

        override fun getLocalNames(): Set<Symbol> = entries.map { it.name }.toSet()
    }

    private val asDeclarationBlock = NamespaceStaticBlock()

    val innerStaticScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerDynamicScope = object : DynamicScope {
        override fun getValue(
            name: Symbol,
        ): Thunk<Value>? = getEntry(name = name)?.valueThunk
    }.chainWith(
        context = prelude.dynamicScope,
    )


    val expressionMap: ExpressionMap = ExpressionMap.unionAllOf(entries) { it.expressionMap }

    val errors: Set<SemanticError> by lazy {
        entries.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun printErrors() {
        errors.forEach { println(it.dump()) }
    }

    override fun dump(): String = "(namespace)"
}

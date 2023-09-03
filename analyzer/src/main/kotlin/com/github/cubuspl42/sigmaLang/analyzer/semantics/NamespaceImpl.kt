package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm

class NamespaceImpl(
    private val prelude: Prelude,
    private val term: NamespaceDefinitionTerm,
) : Namespace {
    override val entries: Set<NamespaceEntry> = term.namespaceEntries.map {
        NamespaceEntry.build(
            containingNamespace = this,
            term = it,
        )
    }.toSet()

    override fun getEntry(
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

    override val asDeclarationBlock = NamespaceStaticBlock()

    override val innerStaticScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = prelude.declarationScope,
    )

    override val innerDynamicScope = object : DynamicScope {
        override fun getValue(
            name: Symbol,
        ): Thunk<Value>? = getEntry(name = name)?.valueThunk
    }.chainWith(
        context = prelude.dynamicScope,
    )


    override val expressionMap: ExpressionMap = ExpressionMap.unionAllOf(entries) { it.expressionMap }

    override val errors: Set<SemanticError> by lazy {
        entries.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    override fun printErrors() {
        errors.forEach { println(it.dump()) }
    }
}

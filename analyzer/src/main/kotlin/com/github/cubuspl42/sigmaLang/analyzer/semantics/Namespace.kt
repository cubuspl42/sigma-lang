package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
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

    private fun getStaticDefinition(
        name: Symbol,
    ): NamespaceEntry? = entries.singleOrNull {
        it.name == name
    }

    fun getConstantDefinition(
        name: Symbol,
    ): ConstantDefinition? = getStaticDefinition(name = name) as? ConstantDefinition

    inner class NamespaceStaticBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = getConstantDefinition(name = name)?.let {
            ResolvedName(
                type = it.asValueDefinition.effectiveValueType, resolution = ConstDefinitionResolution(
                    constantDefinition = it,
                )
            )
        }
    }

    private val asDeclarationBlock = NamespaceStaticBlock()

    val innerStaticScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerScope = object : Scope {
        override fun getValue(
            name: Symbol,
        ): Thunk<Value>? {
            val constantDefinition = getStaticDefinition(name = name) as? ConstantDefinition
            return constantDefinition?.staticValue
        }
    }.chainWith(
        context = prelude.scope,
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

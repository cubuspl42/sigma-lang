package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm

class Namespace(
    private val prelude: Prelude,
    private val term: NamespaceDefinitionSourceTerm,
) : Value() {
    companion object {
        fun build(
            prelude: Prelude,
            term: NamespaceDefinitionSourceTerm,
        ): Namespace = Namespace(
            prelude = prelude,
            term = term,
        )
    }

    private val staticDefinitions: Set<StaticDefinition> = term.namespaceEntries.map {
        StaticDefinition.build(
            containingNamespace = this,
            term = it,
        )
    }.toSet()

    private fun getStaticDefinition(
        name: Symbol,
    ): StaticDefinition? = staticDefinitions.singleOrNull {
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
                type = it.asValueDefinition.effectiveValueType,
                resolution = ConstDefinitionResolution(
                    constantDefinition = it,
                )
            )
        }
    }

    private val asDeclarationBlock = NamespaceStaticBlock()

    val innerDeclarationScope: StaticScope = asDeclarationBlock.chainWith(
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

    val errors: Set<SemanticError> by lazy {
        staticDefinitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun printErrors() {
        errors.forEach { println(it.dump()) }
    }

    override fun dump(): String = "(namespace)"
}

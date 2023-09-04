package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm

class NamespaceDefinition(
    private val prelude: Prelude,
    private val term: NamespaceDefinitionTerm,
) {
    companion object {
        fun build(
            prelude: Prelude,
            term: NamespaceDefinitionTerm,
        ): NamespaceDefinition = NamespaceDefinition(
            prelude = prelude,
            term = term,
        )
    }

    val definitions: Set<ConstantDefinition> = term.namespaceEntries.map {
        ConstantDefinition.build(
            containingNamespaceDefinition = this,
            term = it,
        )
    }.toSet()

    fun getDefinition(
        name: Symbol,
    ): ConstantDefinition? = definitions.singleOrNull {
        it.name == name
    }

    inner class NamespaceStaticBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvableDeclaration? = getDefinition(name = name)?.let {
            ResolvableDeclaration(
                type = it.effectiveType,
                expressionClassification = ConstClassification(
                    constantDefinition = it,
                ),
            )
        }

        override fun getLocalNames(): Set<Symbol> = definitions.map { it.name }.toSet()
    }

    private val asDeclarationBlock = NamespaceStaticBlock()

    val innerStaticScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = prelude.declarationScope,
    )

    val innerDynamicScope = object : DynamicScope {
        override fun getValue(
            name: Symbol,
        ): Thunk<Value>? = getDefinition(name = name)?.valueThunk
    }.chainWith(
        context = prelude.dynamicScope,
    )


    val expressionMap: ExpressionMap = ExpressionMap.unionAllOf(definitions) { it.expressionMap }

    val errors: Set<SemanticError> by lazy {
        definitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun printErrors() {
        errors.forEach { println(it.dump()) }
    }
}

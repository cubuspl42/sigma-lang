package sigma.semantics

import sigma.evaluation.values.Symbol

abstract class DeclarationBlock : StaticScope {
    abstract fun getDeclaration(name: Symbol): Declaration?

    final override fun resolveName(
        name: Symbol,
    ): Declaration? = getDeclaration(name = name)

    fun chainWith(outerScope: StaticScope): StaticScope = StaticScope.Chained(
        outerScope = outerScope,
        declarationBlock = this,
    )
}

package sigma.semantics

import sigma.evaluation.values.Symbol

abstract class DeclarationBlock : DeclarationScope {
    abstract fun getDeclaration(name: Symbol): Declaration?

    final override fun resolveDeclaration(
        name: Symbol,
    ): Declaration? = getDeclaration(name = name)

    fun chainWith(outerScope: DeclarationScope): DeclarationScope = DeclarationScope.Chained(
        outerScope = outerScope,
        declarationBlock = this,
    )
}

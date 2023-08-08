package sigma.semantics

import sigma.evaluation.values.Symbol

abstract class StaticBlock : StaticScope {
    abstract fun resolveNameLocally(name: Symbol): ResolvedName?

    final override fun resolveName(
        name: Symbol,
    ): ResolvedName? = resolveNameLocally(name = name)

    fun chainWith(outerScope: StaticScope): StaticScope = StaticScope.Chained(
        outerScope = outerScope,
        staticBlock = this,
    )
}

package sigma

import sigma.parser.antlr.SigmaParser.ScopeContext

data class ScopeConstructor(
    val binds: Map<Symbol, Expression>,
) : Expression {
    companion object {
        fun build(
            scope: ScopeContext,
        ): ScopeConstructor = ScopeConstructor(
            binds = scope.bind().associate {
                Symbol.of(it.name.text) to Expression.build(it.bound)
            },
        )
    }

    fun link(
        parent: Scope,
    ): LinkedScope = LinkedScope(
        parent = parent,
        binds = binds,
    )

    override fun evaluate(
        scope: Scope,
    ): Value = link(parent = scope)

    override fun dump(): String = "(scope constructor)"
}

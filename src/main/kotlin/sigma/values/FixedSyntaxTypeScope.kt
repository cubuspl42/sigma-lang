package sigma.values

import sigma.SyntaxTypeScope
import sigma.semantics.types.Type

data class FixedSyntaxTypeScope(
    private val entries: Map<Symbol, Type>,
) : SyntaxTypeScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = entries[typeName]
}

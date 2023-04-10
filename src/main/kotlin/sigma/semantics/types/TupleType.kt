package sigma.semantics.types

import sigma.SyntaxValueScope

abstract class TupleType : TableType() {
    abstract fun toStaticValueScope(): SyntaxValueScope

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType
}

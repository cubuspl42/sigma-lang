package sigma.semantics.types

import sigma.SyntaxValueScope
import sigma.semantics.expressions.Abstraction

abstract class TupleType : TableType() {
    abstract fun toStaticValueScope(): SyntaxValueScope

    abstract fun toArgumentDeclarationBlock(): Abstraction.ArgumentDeclarationBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType
}

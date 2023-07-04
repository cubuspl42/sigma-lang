package sigma.semantics.types

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.tables.Table
import sigma.semantics.expressions.Abstraction

abstract class TupleType : TableType() {
    abstract fun toArgumentDeclarationBlock(): Abstraction.ArgumentDeclarationBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: Table): Scope
}

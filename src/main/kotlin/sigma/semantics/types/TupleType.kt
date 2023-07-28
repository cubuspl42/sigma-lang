package sigma.semantics.types

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.DictValue
import sigma.semantics.expressions.Abstraction

abstract class TupleType : TableType() {
    abstract fun toArgumentDeclarationBlock(): Abstraction.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): Scope
}

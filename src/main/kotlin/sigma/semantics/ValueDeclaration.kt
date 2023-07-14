package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type

abstract class ValueDeclaration {
    abstract val name: Symbol

    abstract val inferredType: Computation<Type>
}

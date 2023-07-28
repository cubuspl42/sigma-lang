package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.semantics.types.Type

interface ValueDeclaration : Declaration {
    val effectiveValueType: Computation<Type>
}


sealed class Binding {
   abstract val type: Type
}

sealed interface Formula {

}

class DynamicBinding(
    override val type: Type,
    val formula: Formula,
) : Binding()

class StaticBinding(
    override val type: Type,
    val thunk: Thunk,
): Binding()
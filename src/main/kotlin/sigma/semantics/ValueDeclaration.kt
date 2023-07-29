package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.semantics.types.Type

interface ValueDeclaration : Declaration {
    val effectiveValueType: Thunk<Type>
}

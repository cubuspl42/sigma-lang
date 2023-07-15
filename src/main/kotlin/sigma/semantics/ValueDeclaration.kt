package sigma.semantics

import sigma.semantics.types.Type

interface ValueDeclaration : Declaration {
    val effectiveValueType: Computation<Type>
}

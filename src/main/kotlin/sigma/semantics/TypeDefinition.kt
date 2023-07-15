package sigma.semantics

import sigma.semantics.types.Type

interface TypeDefinition : Declaration {
    val definedType: Type
}

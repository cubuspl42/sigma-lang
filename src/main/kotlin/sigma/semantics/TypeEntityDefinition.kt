package sigma.semantics

import sigma.semantics.types.TypeEntity

interface TypeEntityDefinition : Declaration {
    val definedTypeEntity: TypeEntity
}

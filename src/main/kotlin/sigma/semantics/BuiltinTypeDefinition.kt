package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type

data class BuiltinTypeDefinition(
    override val name: Symbol,
    override val definedTypeEntity: Type,
) : TypeEntityDefinition

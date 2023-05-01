package utils

import sigma.Computation
import sigma.semantics.Declaration
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.Type
import sigma.values.Symbol

class FakeDeclaration(
    override val name: Symbol,
    type: Type,
) : Declaration() {

    override val inferredValueType: Computation<Type> = Computation.pure(type)

    override val errors: Set<SemanticError> = emptySet()
}

class FakeDeclarationScope(
    typeByName: Map<String, Type>,
) : DeclarationScope {
    private val declarationByScope = typeByName.entries.associate { (name, type) ->
        val nameSymbol = Symbol.of(name)

        nameSymbol to FakeDeclaration(
            name = nameSymbol,
            type = type,
        )
    }

    override fun resolveDeclaration(name: Symbol): Declaration? = declarationByScope[name]
}
package utils

import sigma.semantics.Computation
import sigma.semantics.ValueDeclaration
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.semantics.DeclarationBlock

class FakeValueDeclaration(
    override val name: Symbol,
    type: Type,
) : ValueDeclaration {
    override val effectiveValueType: Computation<Type> = Computation.pure(type)
}

class FakeDeclarationBlock(
    typeByName: Map<String, Type>,
) : DeclarationBlock() {
    private val declarationByScope = typeByName.entries.associate { (name, type) ->
        val nameSymbol = Symbol.of(name)

        nameSymbol to FakeValueDeclaration(
            name = nameSymbol,
            type = type,
        )
    }

    override fun getDeclaration(name: Symbol): ValueDeclaration? = declarationByScope[name]
}

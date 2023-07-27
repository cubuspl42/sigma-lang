package utils

import sigma.semantics.Computation
import sigma.semantics.ValueDeclaration
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.semantics.Declaration
import sigma.semantics.DeclarationBlock
import sigma.semantics.types.TypeEntity

data class FakeValueDeclaration(
    override val name: Symbol,
    val type: Type,
) : ValueDeclaration {
    override val effectiveValueType: Computation<Type> = Computation.pure(type)
}

class FakeDeclarationBlock(
    declarations: Set<Declaration>,
) : DeclarationBlock() {
    companion object {
        fun of(
            vararg declarations: Declaration,
        ): FakeDeclarationBlock = FakeDeclarationBlock(
            declarations = declarations.toSet(),
        )
    }

    private val declarationByName = declarations.associateBy { it.name }

    override fun getDeclaration(name: Symbol): Declaration? = declarationByName[name]
}

package utils

import sigma.semantics.Computation
import sigma.semantics.ValueDeclaration
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.semantics.BuiltinResolution
import sigma.semantics.Declaration
import sigma.semantics.DynamicResolution
import sigma.semantics.ResolvedName
import sigma.semantics.StaticBlock

data class FakeValueDeclaration(
    override val name: Symbol,
    val type: Type,
) : ValueDeclaration {
    override val effectiveValueType: Computation<Type> = Computation.pure(type)
}

class FakeStaticBlock(
    declarations: Set<FakeValueDeclaration>,
) : StaticBlock() {
    companion object {
        fun of(
            vararg declarations: FakeValueDeclaration,
        ): FakeStaticBlock = FakeStaticBlock(
            declarations = declarations.toSet(),
        )
    }

    private val declarationByName = declarations.associateBy { it.name }

    override fun resolveNameLocally(name: Symbol): ResolvedName? = declarationByName[name]?.let {
        ResolvedName(
            type = it.type,
            resolution = DynamicResolution(),
        )
    }
}

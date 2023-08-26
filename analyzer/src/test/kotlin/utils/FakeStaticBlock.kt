package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ValueDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.DynamicResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock

data class FakeValueDeclaration(
    override val name: Symbol,
    val type: Type,
) : ValueDeclaration {
    override val effectiveValueType: Thunk<Type> = Thunk.pure(type)
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

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedName? = declarationByName[name]?.let {
        ResolvedName(
            type = it.type.asThunk,
            resolution = DynamicResolution(
                resolvedFormula = Formula(
                    name = name,
                ),
            ),
        )
    }

    override fun getLocalNames(): Set<Symbol> = declarationByName.keys
}

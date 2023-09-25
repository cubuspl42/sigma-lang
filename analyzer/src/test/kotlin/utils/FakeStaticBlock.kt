package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration

data class FakeUserDeclaration(
    override val name: Symbol,
    val type: MembershipType,
) : UserDeclaration {
    override val annotatedTypeThunk: Thunk<MembershipType> = Thunk.pure(type)

    override val effectiveTypeThunk: Thunk<MembershipType> = Thunk.pure(type)

    override val errors: Set<SemanticError> = emptySet()
}

class FakeStaticBlock(
    declarations: Set<FakeUserDeclaration>,
) : StaticBlock() {
    companion object {
        fun of(
            vararg declarations: FakeUserDeclaration,
        ): FakeStaticBlock = FakeStaticBlock(
            declarations = declarations.toSet(),
        )
    }

    private val declarationByName = declarations.associateBy { it.name }

    override fun resolveNameLocally(
        name: Symbol,
    ): ClassifiedIntroduction? = declarationByName[name]

    override fun getLocalNames(): Set<Symbol> = declarationByName.keys
}

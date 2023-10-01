package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserDeclaration

data class FakeUserDeclaration(
    override val name: Symbol,
    val type: MembershipType,
) : UserDeclaration {
    override val annotatedType = type

    override val computedEffectiveType: Expression.Computation<MembershipType> = Expression.Computation.pure(type)

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
    ): Introduction? = declarationByName[name]

    override fun getLocalNames(): Set<Symbol> = declarationByName.keys
}

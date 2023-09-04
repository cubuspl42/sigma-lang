package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.UserDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvableDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableDeclaration

data class FakeUserDeclaration(
    override val name: Symbol,
    val type: Type,
) : VariableDeclaration(), UserDeclaration {
    override val annotatedTypeThunk: Thunk<Type> = Thunk.pure(type)

    override val declaredTypeThunk: Thunk<Type> = Thunk.pure(type)

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
    ): ResolvableDeclaration? = declarationByName[name]

    override fun getLocalNames(): Set<Symbol> = declarationByName.keys
}

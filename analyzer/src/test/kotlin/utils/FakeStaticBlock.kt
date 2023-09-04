package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvableDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock

data class FakeDeclaration(
    override val name: Symbol,
    val type: Type,
) : Declaration, ResolvableDeclaration {
    override val effectiveValueType: Thunk<Type> = Thunk.pure(type)

    override val resolvedType: Thunk<Type> = Thunk.pure(type)

    override val expressionClassification: ExpressionClassification = VariableClassification(
        resolvedFormula = Formula(name = name),
    )
}

class FakeStaticBlock(
    declarations: Set<FakeDeclaration>,
) : StaticBlock() {
    companion object {
        fun of(
            vararg declarations: FakeDeclaration,
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

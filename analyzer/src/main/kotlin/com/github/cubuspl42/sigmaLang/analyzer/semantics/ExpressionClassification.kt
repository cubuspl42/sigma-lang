package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value

data class Formula(
    val name: Symbol,
) {
    companion object {
        fun of(s: String): Formula = Formula(
            name = Symbol.of(s)
        )
    }
}

sealed class ExpressionClassification

class VariableClassification(
    val resolvedFormula: Formula?,
) : ExpressionClassification()

class ConstClassification(
    private val constantDefinition: ConstantDefinition,
) : ExpressionClassification() {
    val resolvedValue: Thunk<Value> = constantDefinition.valueThunk
}

interface ClassifiedDeclaration {
    val expressionClassification: ExpressionClassification
}

// TODO: Sealed
interface ResolvableDeclaration : Declaration, ClassifiedDeclaration {
    override val name: Symbol
}

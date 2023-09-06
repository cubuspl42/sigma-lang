package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition

sealed class ExpressionClassification

class VariableClassification(
    val resolvedFormula: Formula?,
) : ExpressionClassification()

class ConstClassification(
    private val constantDefinition: ConstantDefinition,
) : ExpressionClassification() {
    val resolvedValue: Thunk<Value> = constantDefinition.valueThunk
}

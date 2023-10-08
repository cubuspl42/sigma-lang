package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassification

interface VariableIntroduction : ClassifiedIntroduction {
    override val expressionClassification: ExpressionClassification
        get() = VariableClassification(
            resolvedFormula = Formula(name = name),
        )
}

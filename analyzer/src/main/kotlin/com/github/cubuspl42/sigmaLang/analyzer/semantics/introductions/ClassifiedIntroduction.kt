package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification

sealed interface ClassifiedIntroduction : Introduction {
    val expressionClassification: ExpressionClassification
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

sealed interface ClassifiedDeclaration : Declaration {
    // TODO: Move to `Expression`
    val expressionClassification: ExpressionClassification
}

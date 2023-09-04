package com.github.cubuspl42.sigmaLang.analyzer.semantics

abstract class VariableDeclaration : ClassifiedDeclaration {
    final override val expressionClassification: ExpressionClassification
        get() = VariableClassification(
            resolvedFormula = Formula(name = name),
        )
}

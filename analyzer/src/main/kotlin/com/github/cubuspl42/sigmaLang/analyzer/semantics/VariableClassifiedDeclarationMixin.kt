package com.github.cubuspl42.sigmaLang.analyzer.semantics

class VariableClassifiedDeclarationMixin(
    namedDeclaration: NamedDeclaration,
) : ClassifiedDeclaration {
    override val expressionClassification: ExpressionClassification = VariableClassification(
        resolvedFormula = Formula(name = namedDeclaration.name),
    )
}

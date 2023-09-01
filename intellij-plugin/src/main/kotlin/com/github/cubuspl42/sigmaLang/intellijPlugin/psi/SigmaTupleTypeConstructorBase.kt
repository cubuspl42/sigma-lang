package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm

interface SigmaTupleTypeConstructorBase : SigmaExpression {
    override val asTerm: TupleTypeConstructorTerm
}

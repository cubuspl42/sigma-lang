package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface ReferenceTerm : ExpressionTerm {
    val referredName: Identifier
}

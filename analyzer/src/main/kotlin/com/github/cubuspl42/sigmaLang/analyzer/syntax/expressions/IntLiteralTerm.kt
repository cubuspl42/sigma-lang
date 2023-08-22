package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue

interface IntLiteralTerm : ExpressionTerm {
    val value: IntValue
}

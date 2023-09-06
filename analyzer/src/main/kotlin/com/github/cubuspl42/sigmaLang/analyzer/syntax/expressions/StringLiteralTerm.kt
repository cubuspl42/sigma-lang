package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue

interface StringLiteralTerm : ExpressionTerm {
    val value: StringValue
}

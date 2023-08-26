package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface FieldReadTerm : ExpressionTerm {
    val subject: ExpressionTerm
    val fieldName: Symbol
}
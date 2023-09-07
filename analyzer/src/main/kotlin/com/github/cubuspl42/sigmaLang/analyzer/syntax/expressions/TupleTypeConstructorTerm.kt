package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Constness

sealed interface TupleTypeConstructorTerm : ExpressionTerm {
    val constness: Constness
}

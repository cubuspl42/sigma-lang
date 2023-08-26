package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

sealed class TupleTypeConstructor : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: TupleTypeConstructorTerm,
        ): TupleTypeConstructor = when (term) {
            is OrderedTupleTypeConstructorTerm -> OrderedTupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is UnorderedTupleTypeConstructorTerm -> UnorderedTupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            else -> throw UnsupportedOperationException("Unsupported term: $term")
        }
    }
}

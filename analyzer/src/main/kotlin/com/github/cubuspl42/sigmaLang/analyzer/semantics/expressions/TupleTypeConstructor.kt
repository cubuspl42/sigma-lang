package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

sealed class TupleTypeConstructor : TypeConstructor() {
    companion object {
        fun build(
            context: BuildContext,
            term: TupleTypeConstructorTerm,
        ): Stub<TupleTypeConstructor> = when (term) {
            is OrderedTupleTypeConstructorTerm -> OrderedTupleTypeConstructor.build(
                context = context,
                term = term,
            )

            is UnorderedTupleTypeConstructorTerm -> UnorderedTupleTypeConstructor.build(
                context = context,
                term = term,
            )

            else -> throw UnsupportedOperationException("Unsupported term: $term")
        }
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm

sealed class TupleTypeConstructor : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: TupleTypeConstructorSourceTerm,
        ): TupleTypeConstructor = when (term) {
            is OrderedTupleTypeConstructorSourceTerm -> OrderedTupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is UnorderedTupleTypeConstructorSourceTerm -> UnorderedTupleTypeConstructor.build(
                outerScope = outerScope,
                term = term,
            )
        }
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm

abstract class TupleConstructor : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: TupleConstructorSourceTerm,
        ): TupleConstructor = when (term) {
            is OrderedTupleConstructorSourceTerm -> OrderedTupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is UnorderedTupleConstructorSourceTerm -> UnorderedTupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )
        }
    }
}

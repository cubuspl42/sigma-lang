package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

abstract class TupleConstructor : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: TupleConstructorTerm,
        ): TupleConstructor = when (term) {
            is OrderedTupleConstructorTerm -> OrderedTupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )

            is UnorderedTupleConstructorTerm -> UnorderedTupleConstructor.build(
                outerScope = outerScope,
                term = term,
            )
        }
    }
}

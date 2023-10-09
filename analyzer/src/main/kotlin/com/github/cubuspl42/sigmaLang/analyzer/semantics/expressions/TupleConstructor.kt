package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

abstract class TupleConstructor : Expression() {
    companion object {
        fun build(
            context: BuildContext,
            term: TupleConstructorTerm,
        ): Stub<TupleConstructor> = when (term) {
            is OrderedTupleConstructorTerm -> OrderedTupleConstructor.build(
                context = context,
                term = term,
            )

            is UnorderedTupleConstructorTerm -> UnorderedTupleConstructor.build(
                context = context,
                term = term,
            )
        }
    }
}

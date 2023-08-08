package sigma.semantics.expressions

import sigma.semantics.StaticScope
import sigma.syntax.expressions.TupleTypeConstructorTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm

sealed class TupleTypeConstructor : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: TupleTypeConstructorTerm,
        ): TupleTypeConstructor = when (term) {
            is OrderedTupleTypeConstructorTerm -> OrderedTupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is UnorderedTupleTypeConstructorTerm -> UnorderedTupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )
        }
    }
}

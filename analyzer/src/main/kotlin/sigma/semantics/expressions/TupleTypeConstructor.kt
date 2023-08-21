package sigma.semantics.expressions

import sigma.semantics.StaticScope
import sigma.syntax.expressions.TupleTypeConstructorSourceTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm

sealed class TupleTypeConstructor : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: TupleTypeConstructorSourceTerm,
        ): TupleTypeConstructor = when (term) {
            is OrderedTupleTypeConstructorSourceTerm -> OrderedTupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is UnorderedTupleTypeConstructorSourceTerm -> UnorderedTupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )
        }
    }
}

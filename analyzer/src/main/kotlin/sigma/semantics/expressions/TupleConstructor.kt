package sigma.semantics.expressions

import sigma.semantics.StaticScope
import sigma.syntax.expressions.OrderedTupleConstructorSourceTerm
import sigma.syntax.expressions.TupleConstructorSourceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorSourceTerm

abstract class TupleConstructor : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: TupleConstructorSourceTerm,
        ): TupleConstructor = when (term) {
            is OrderedTupleConstructorSourceTerm -> OrderedTupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is UnorderedTupleConstructorSourceTerm -> UnorderedTupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )
        }
    }
}

package sigma.semantics.expressions

import sigma.semantics.DeclarationScope
import sigma.syntax.expressions.OrderedTupleConstructorTerm
import sigma.syntax.expressions.TupleConstructorTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm

abstract class TupleConstructor : Expression() {
    companion object {
        fun build(
            declarationScope: DeclarationScope,
            term: TupleConstructorTerm,
        ): TupleConstructor = when (term) {
            is OrderedTupleConstructorTerm -> OrderedTupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )

            is UnorderedTupleConstructorTerm -> UnorderedTupleConstructor.build(
                declarationScope = declarationScope,
                term = term,
            )
        }
    }
}

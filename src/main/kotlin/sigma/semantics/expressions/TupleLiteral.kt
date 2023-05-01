package sigma.semantics.expressions

import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.syntax.expressions.OrderedTupleLiteralTerm
import sigma.syntax.expressions.TupleLiteralTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm

abstract class TupleLiteral : Expression() {
    companion object {
        fun build(
            declarationScope: DeclarationScope,
            typeScope: TypeScope,
            term: TupleLiteralTerm,
        ): TupleLiteral = when (term) {
            is OrderedTupleLiteralTerm -> OrderedTupleLiteral.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )

            is UnorderedTupleLiteralTerm -> UnorderedTupleLiteral.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term,
            )
        }
    }
}

package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm

interface LocalDefinitionTerm : DefinitionTerm {
    override val name: Symbol

    override val declaredTypeBody: ExpressionSourceTerm?

    override val body: ExpressionSourceTerm
}

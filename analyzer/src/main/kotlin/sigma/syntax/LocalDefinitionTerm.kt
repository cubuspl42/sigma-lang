package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.ExpressionTerm

interface LocalDefinitionTerm : DefinitionTerm {
    override val name: Symbol

    override val declaredTypeBody: ExpressionTerm?

    override val body: ExpressionTerm
}

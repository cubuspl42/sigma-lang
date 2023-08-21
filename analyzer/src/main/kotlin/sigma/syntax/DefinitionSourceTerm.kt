package sigma.syntax

import sigma.syntax.expressions.ExpressionSourceTerm

sealed interface DefinitionSourceTerm : DefinitionTerm {
    override val declaredTypeBody: ExpressionSourceTerm?
    override val body: ExpressionSourceTerm
}

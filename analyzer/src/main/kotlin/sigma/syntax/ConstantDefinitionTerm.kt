package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.ExpressionTerm

interface ConstantDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val declaredTypeBody: ExpressionTerm?

    val body: ExpressionTerm
}

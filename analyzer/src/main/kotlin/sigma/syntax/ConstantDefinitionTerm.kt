package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm

interface ConstantDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val declaredTypeBody: ExpressionSourceTerm?

    val body: ExpressionSourceTerm
}

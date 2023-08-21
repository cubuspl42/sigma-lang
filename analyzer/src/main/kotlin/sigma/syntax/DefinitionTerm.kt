package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm

sealed interface DefinitionTerm {
    val name: Symbol
    val declaredTypeBody: ExpressionSourceTerm?
    val body: ExpressionSourceTerm
}

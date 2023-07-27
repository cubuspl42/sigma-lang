package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionTerm

sealed interface DefinitionTerm {
    val name: Symbol
    val declaredTypeBody: ExpressionTerm?
    val body: ExpressionTerm
}

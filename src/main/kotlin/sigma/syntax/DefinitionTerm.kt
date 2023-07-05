package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

sealed interface DefinitionTerm {
    val name: Symbol
    val type: TypeExpressionTerm?
    val definer: ExpressionTerm
}

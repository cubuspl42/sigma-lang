package sigma.syntax.expressions

import sigma.syntax.LocalDefinitionTerm

interface LetExpressionTerm {
    val definitions: List<LocalDefinitionTerm>

    val result: ExpressionTerm
}

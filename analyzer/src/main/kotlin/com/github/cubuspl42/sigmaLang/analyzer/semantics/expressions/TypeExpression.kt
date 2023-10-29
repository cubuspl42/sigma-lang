package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object TypeExpression {
    data class DiagnosedAnalysis(
        val type: TypeAlike?,
        val errors: Set<SemanticError>,
    ) {
        val typeOrIllType: TypeAlike
            get() = type ?: IllType
    }

    data class TypeEvaluationError(
        val evaluationError: EvaluationError,
    ) : SemanticError

    data class NonTypeValueError(
        val value: Value,
    ) : SemanticError

    fun build(
        outerScope: StaticScope,
        term: ExpressionTerm,
    ): Stub<Expression> = Expression.build(
        context = Expression.BuildContext(
            outerScope = outerScope, // TODO: Shift
        ),
        term = term,
    )
}

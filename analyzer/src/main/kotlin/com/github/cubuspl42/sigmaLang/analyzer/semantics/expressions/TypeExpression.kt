package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object TypeExpression {
    data class DiagnosedAnalysis(
        val type: MembershipType?,
        val errors: Set<SemanticError>,
    ) {
        val typeOrIllType: MembershipType
            get() = type ?: IllType
    }

    data class TypeEvaluationError(
        val evaluationError: EvaluationError,
    ) : SemanticError

    data class NonTypeValueError(
        val value: Value,
    ) : SemanticError

    fun build(
        outerMetaScope: StaticScope,
        term: ExpressionTerm,
    ): Expression.Stub<Expression> = Expression.build(
        context = Expression.BuildContext(
            outerMetaScope = BuiltinScope,
            outerScope = outerMetaScope,
        ),
        term = term,
    )
}

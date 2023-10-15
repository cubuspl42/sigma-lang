package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object UserVariableDefinition {
    fun build(
        context: Expression.BuildContext,
        term: DefinitionTerm,
    ): Definition {
        val declaredTypeStub = term.declaredTypeBody?.let {
            TypeExpression.build(
                outerMetaScope = context.outerMetaScope,
                term = it,
            )
        }

        val bodyStub = Expression.build(
            context = context,
            term = term.body,
        )

        return object : Definition {
            override val name: Symbol = term.name

            override val bodyStub: Expression.Stub<Expression> = object : Expression.Stub<Expression> {
                override val resolved: Expression by lazy {
                    val body = bodyStub.resolved

                    if (declaredTypeStub != null) {
                        val declaredTypeAnalysis = declaredTypeStub.resolved.analyzeAsType(
                            outerScope = context.outerScope,
                        )

                        TypeAnnotatedBody(
                            outerScope = context.outerScope,
                            declaredTypeAnalysis = declaredTypeAnalysis,
                            body = body,
                        )
                    } else {
                        body
                    }
                }
            }
        }
    }
}

class TypeAnnotatedBody(
    override val outerScope: StaticScope,
    private val declaredTypeAnalysis: TypeExpression.DiagnosedAnalysis,
    private val body: Expression,
) : Expression() {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: MembershipType.MatchResult,
    ) : SemanticError

    private val declaredType = declaredTypeAnalysis?.typeOrIllType

    override val term: ExpressionTerm? = null

    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = buildDiagnosedAnalysisComputation {

        val bodyAnalysis = compute(body.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val inferredType = bodyAnalysis.inferredType

        val unmatchedInferredTypeError = if (declaredType != null) {
            val matchResult = declaredType.match(inferredType)

            if (matchResult.isFull()) null
            else UnmatchedInferredTypeError(
                location = body.location,
                matchResult = matchResult,
            )
        } else null

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = declaredType ?: inferredType,
            ),
            directErrors = declaredTypeAnalysis.errors + setOfNotNull(
                unmatchedInferredTypeError,
            ),
        )
    }

    override val subExpressions: Set<Expression> = setOf(body)

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = body.bind(
        dynamicScope = dynamicScope,
    )
}

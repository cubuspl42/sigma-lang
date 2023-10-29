package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FirstOrderExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object UserVariableDefinition {
    fun build(
        context: Expression.BuildContext,
        term: DefinitionTerm,
    ): ResolvedDefinition {
        val declaredTypeStub = term.declaredTypeBody?.let {
            TypeExpression.build(
                outerScope = context.outerScope,
                term = it,
            )
        }

        val bodyStub = Expression.build(
            context = context,
            term = term.body,
        )

        val definition = object : Definition {
            override val bodyStub: Stub<Expression> = object : Stub<Expression> {
                override val resolved: Expression by lazy {
                    val body = bodyStub.resolved

                    if (declaredTypeStub != null) {
                        val declaredTypeAnalysis = declaredTypeStub.resolved.evaluateAsType()

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

        return ResolvedDefinition(
            definition = definition,
        )
    }
}

class TypeAnnotatedBody(
    override val outerScope: StaticScope,
    private val declaredTypeAnalysis: TypeExpression.DiagnosedAnalysis,
    private val body: Expression,
) : FirstOrderExpression() {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: SpecificType.MatchResult,
    ) : SemanticError

    private val declaredType = declaredTypeAnalysis.typeOrIllType

    override val term: ExpressionTerm? = null

    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = buildDiagnosedAnalysisComputation {
        val bodyAnalysis = compute(body.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val inferredType = bodyAnalysis.inferredType

        val unmatchedInferredTypeError = run {
            val matchResult = declaredType.match(inferredType as SpecificType)

            if (matchResult.isFull()) null
            else UnmatchedInferredTypeError(
                location = body.location,
                matchResult = matchResult,
            )
        }

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

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = body.bind(
        dynamicScope = dynamicScope,
    )
}

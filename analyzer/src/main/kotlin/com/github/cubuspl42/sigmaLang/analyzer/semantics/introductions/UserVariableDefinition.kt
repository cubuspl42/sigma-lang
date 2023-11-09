package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedOrderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FirstOrderExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.TypeExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.asLazy
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.MethodDefinitionTerm
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

        val innerBodyLazy = Expression.build(
            context = context,
            term = term.body,
        ).asLazy()

        val outerBodyLazy = lazy {
            val body = innerBodyLazy.value

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

        return ResolvedDefinition(
            bodyLazy = outerBodyLazy,
        )
    }

    fun buildMethod(
        context: Expression.BuildContext,
        term: MethodDefinitionTerm,
    ): ResolvedDefinition = ResolvedDefinition(
        bodyLazy = object {
            val instanceTypeDiagnosedAnalysis by Expression.buildType(
                context = context,
                typeTerm = term.instanceType,
            )

            val instanceType: TypeAlike by lazy {
                instanceTypeDiagnosedAnalysis.type ?: IllType
            }

            val argumentDeclaration by lazy {
                AbstractionConstructor.ArgumentDeclaration(
                    declaredType = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Identifier.of("this"),
                                type = instanceType,
                            ),
                        ),
                    ),
                )
            }

            val methodExtractorConstructorLazy = lazy {
                AbstractionConstructor(
                    argumentDeclaration = argumentDeclaration,
                    declaredImageTypeLazy = lazyOf(null),
                    imageLazy = methodConstructorLazy,
                )
            }

            val methodConstructorLazy = AbstractionConstructor.build(
                context = Expression.BuildContext(
                    outerScope = StaticBlock.Fixed(
                        resolvedNameByName = mapOf(
                            Identifier.of("this") to LeveledResolvedIntroduction.primaryIntroduction(
                                resolvedIntroduction = ResolvedOrderedArgument(
                                    index = 0L,
                                    argumentDeclaration = argumentDeclaration,
                                ),
                            ),
                        ),
                    ).chainWith(context.outerScope)
                ),
                term = term.body,
            ).expressionLazy
        }.methodExtractorConstructorLazy,
    )
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

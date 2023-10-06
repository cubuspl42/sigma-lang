package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.BinaryOperationPrototype
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.*
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

class Call(
    override val outerScope: StaticScope,
    override val term: CallTerm,
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: CallTerm,
        ): Call {
            return when (term) {
                is InfixCallTerm -> buildInfix(outerScope, term)
                is PostfixCallTerm -> buildPostfix(outerScope, term)
                else -> throw UnsupportedOperationException("Unsupported call term: $term")
            }
        }

        private fun buildPostfix(
            outerScope: StaticScope,
            term: PostfixCallTerm,
        ): Call = Call(
            outerScope = outerScope,
            term = term,
            subject = build(
                outerScope = outerScope,
                term = term.subject,
            ),
            argument = build(
                outerScope = outerScope,
                term = term.argument,
            ),
        )

        private fun buildInfix(
            outerScope: StaticScope,
            term: InfixCallTerm,
        ): Call {
            val prototype = BinaryOperationPrototype.build(term.operator)

            val leftArgument = Expression.build(
                outerScope = outerScope,
                term = term.leftArgument,
            )

            val rightArgument = Expression.build(
                outerScope = outerScope,
                term = term.rightArgument,
            )

            return Call(
                outerScope = outerScope,
                term = term,
                subject = Reference(
                    outerScope = outerScope,
                    referredName = Symbol.of(prototype.functionName),
                    term = null,
                ),
                argument = UnorderedTupleConstructor(
                    outerScope = outerScope,
                    term = null,
                    entries = setOf(
                        UnorderedTupleConstructor.Entry(
                            name = prototype.leftArgument,
                            value = leftArgument,
                        ),
                        UnorderedTupleConstructor.Entry(
                            name = prototype.rightArgument,
                            value = rightArgument,
                        ),
                    ),
                ),
            )
        }
    }

    data class NonFullyInferredCalleeTypeError(
        override val location: SourceLocation?,
        val calleeGenericType: FunctionType,
        val nonInferredTypeVariables: Set<TypeVariable>,
    ) : SemanticError

    data class NonFunctionCallError(
        override val location: SourceLocation?,
        val illegalSubjectType: MembershipType,
    ) : SemanticError

    data class InvalidArgumentError(
        override val location: SourceLocation?,
        val matchResult: MembershipType.MatchResult,
    ) : SemanticError {
        override fun dump(): String = "$location: Invalid argument: ${matchResult.dump()}"
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectAnalysis = compute(subject.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val argumentAnalysis = compute(argument.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val subjectType = subjectAnalysis.inferredType
        val argumentType = argumentAnalysis.inferredType

        when (subjectType) {
            is FunctionType -> {
                val typeVariableResolution = subjectType.argumentType.resolveTypeVariables(
                    assignedType = argumentType,
                )

                val effectiveArgumentType = subjectType.argumentType.substituteTypeVariables(
                    resolution = typeVariableResolution,
                )

                val effectiveImageType = subjectType.imageType.substituteTypeVariables(
                    resolution = typeVariableResolution,
                )

                val remainingTypeVariables =
                    subjectType.typeVariables - typeVariableResolution.resolvedTypeVariables

                val nonInferredTypeVariables = subjectType.typeVariables.intersect(remainingTypeVariables)

                when {
                    nonInferredTypeVariables.isEmpty() -> {
                        DiagnosedAnalysis(
                            analysis = Analysis(
                                inferredType = effectiveImageType,
                            ),
                            directErrors = setOfNotNull(
                                run {
                                    val matchResult = effectiveArgumentType.match(
                                        assignedType = argumentType,
                                    )

                                    if (!matchResult.isFull()) {
                                        InvalidArgumentError(
                                            location = argument.location,
                                            matchResult = matchResult,
                                        )
                                    } else {
                                        null
                                    }
                                },
                            ),
                        )
                    }

                    else -> {
                        DiagnosedAnalysis.fromError(
                            NonFullyInferredCalleeTypeError(
                                location = subject.location,
                                calleeGenericType = subjectType,
                                nonInferredTypeVariables = nonInferredTypeVariables,
                            ),
                        )
                    }
                }
            }

            else -> DiagnosedAnalysis.fromError(
                NonFunctionCallError(
                    location = subject.location,
                    illegalSubjectType = subjectType,
                )
            )
        }
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.transform2(
            context1 = subject.classifiedValue,
            context2 = argument.classifiedValue,
            combine = { subjectValue, argumentValue ->
                if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

                subjectValue.apply(
                    argument = argumentValue,
                )
            },
        )
    }

    override val subExpressions: Set<Expression> = setOf(subject, argument)

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.combine2(
        subject.bind(
            dynamicScope = dynamicScope,
        ), argument.bind(
            dynamicScope = dynamicScope,
        )
    ) { subjectValue, argumentValue ->
        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        subjectValue.apply(
            argument = argumentValue,
        )
    }.thenDo { it }
}

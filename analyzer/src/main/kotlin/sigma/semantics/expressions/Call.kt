package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.EvaluationOutcome
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.evaluateValueHacky
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.CallTerm

class Call(
    override val term: CallTerm,
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: CallTerm,
        ): Call = Call(
            term = term,
            subject = build(
                declarationScope = declarationScope,
                term = term.subject,
            ),
            argument = build(
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    sealed interface SubjectCallOutcome

    data class LegalSubjectCallResult(
        val effectiveCalleeArgumentType: Type,
        val effectiveResultType: Type,
        val passedArgumentType: Type,
    ) : SubjectCallOutcome

    data class NonFullyInferredCalleeTypeError(
        override val location: SourceLocation,
        val calleeGenericType: FunctionType,
        val nonInferredTypeVariables: Set<TypeVariable>,
    ) : SubjectCallOutcome, SemanticError

    data class NonFunctionCallError(
        override val location: SourceLocation,
        val illegalSubjectType: Type,
    ) : SubjectCallOutcome, SemanticError

    sealed interface ArgumentValidationOutcome

    data object ValidArgumentResult : ArgumentValidationOutcome

    data class InvalidArgumentError(
        override val location: SourceLocation,
        val matchResult: Type.MatchResult,
    ) : ArgumentValidationOutcome, SemanticError {
        override fun dump(): String = "$location: Invalid argument: ${matchResult.dump()}"
    }

    private val subjectCallOutcome: Thunk<SubjectCallOutcome> = Thunk.lazy {
        Thunk.combine2(
            subject.inferredType,
            argument.inferredType,
        ) {
                subjectType,
                argumentType,
            ->

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
                        subjectType.genericParameters - typeVariableResolution.resolvedTypeVariables

                    val nonInferredTypeVariables = subjectType.genericParameters.intersect(remainingTypeVariables)

                    if (nonInferredTypeVariables.isEmpty()) {
                        LegalSubjectCallResult(
                            effectiveCalleeArgumentType = effectiveArgumentType,
                            effectiveResultType = effectiveImageType,
                            passedArgumentType = argumentType,
                        )
                    } else {
                        NonFullyInferredCalleeTypeError(
                            location = subject.location,
                            calleeGenericType = subjectType,
                            nonInferredTypeVariables = nonInferredTypeVariables,
                        )
                    }
                }

                else -> NonFunctionCallError(
                    location = subject.location,
                    illegalSubjectType = subjectType,
                )
            }
        }
    }

    private val argumentValidationOutcome: Thunk<ArgumentValidationOutcome?> by lazy {
        this.subjectCallOutcome.thenJust { subjectCallOutcome ->
            when (subjectCallOutcome) {
                is LegalSubjectCallResult -> {
                    // Thought: Should argument validation and type variable resolution be merged?

                    val effectiveArgumentType = subjectCallOutcome.effectiveCalleeArgumentType
                    val argumentType = subjectCallOutcome.passedArgumentType

                    val matchResult = effectiveArgumentType.match(
                        assignedType = argumentType,
                    )

                    when {
                        matchResult.isFull() -> ValidArgumentResult

                        else -> InvalidArgumentError(
                            location = argument.location,
                            matchResult = matchResult,
                        )
                    }
                }

                else -> null
            }
        }
    }

    override val inferredType: Thunk<Type> by lazy {
        subjectCallOutcome.thenJust { subjectCall ->
            if (subjectCall is LegalSubjectCallResult) {
                subjectCall.effectiveResultType
            } else {
                IllType
            }
        }
    }

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.combine2(
        subject.bind(
            scope = scope,
        ), argument.bind(
            scope = scope,
        )
    ) { subjectValue, argumentValue ->
        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        subjectValue.apply(
            argument = argumentValue,
        )
    }.thenDo { it }

    override val errors: Set<SemanticError> by lazy {
        subject.errors + argument.errors + setOfNotNull(
            subjectCallOutcome.value as? SemanticError,
            argumentValidationOutcome.value as? InvalidArgumentError,
        )
    }
}
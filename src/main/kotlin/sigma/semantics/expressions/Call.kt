package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.CallTerm

class Call(
    override val term: CallTerm,
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: DeclarationScope,
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
        val calleeType: FunctionType,
        val argumentType: Type,
    ) : SubjectCallOutcome

    data class IllegalSubjectCallError(
        override val location: SourceLocation,
        val illegalSubjectType: Type,
    ) : SubjectCallOutcome, SemanticError

    sealed interface ArgumentValidationOutcome

    object ValidArgumentResult : ArgumentValidationOutcome

    data class InvalidArgumentError(
        override val location: SourceLocation,
        val matchResult: Type.MatchResult,
    ) : ArgumentValidationOutcome, SemanticError {
        override fun dump(): String = "$location: Invalid argument: ${matchResult.dump()}"
    }

    private val subjectCallOutcome: Computation<SubjectCallOutcome> = Computation.combine2(
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

                val effectiveSubjectType = subjectType.substituteTypeVariables(
                    resolution = typeVariableResolution,
                )

                LegalSubjectCallResult(
                    calleeType = effectiveSubjectType,
                    argumentType = argumentType,
                )
            }

            else -> IllegalSubjectCallError(
                location = subject.location,
                illegalSubjectType = subjectType,
            )
        }
    }

    private val argumentValidationOutcome: Computation<ArgumentValidationOutcome?> =
        this.subjectCallOutcome.thenJust { subjectCallOutcome ->
            when (subjectCallOutcome) {
                is LegalSubjectCallResult -> {
                    val subjectType = subjectCallOutcome.calleeType
                    val argumentType = subjectCallOutcome.argumentType

                    val matchResult = subjectType.argumentType.match(
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

                is IllegalSubjectCallError -> null
            }
        }

    override val inferredType: Computation<Type> by lazy {
        subjectCallOutcome.thenJust { subjectCall ->
            if (subjectCall is LegalSubjectCallResult) {
                subjectCall.calleeType.imageType
            } else {
                IllType
            }
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            subjectCallOutcome.value as? IllegalSubjectCallError,
            argumentValidationOutcome.value as? InvalidArgumentError,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value {
        val subjectValue = subject.evaluate(scope = scope)

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(scope = scope)

        // Thought: Obtaining argument here might not be lazy enough
        val image = subjectValue.apply(
            argument = argumentValue,
        )

        return image
    }
}

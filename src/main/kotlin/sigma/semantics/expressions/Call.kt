package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.syntax.expressions.CallTerm

class Call(
    override val term: CallTerm,
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: CallTerm,
        ): Call = Call(
            term = term,
            subject = build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.subject,
            ),
            argument = build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    sealed interface SubjectCallOutcome

    data class LegalSubjectCallResult(
        val calleeType: FunctionType,
    ) : SubjectCallOutcome

    data class IllegalSubjectCallError(
        val illegalSubjectType: Type,
    ) : SubjectCallOutcome, SemanticError

    sealed interface ArgumentValidationOutcome

    object ValidArgumentResult : ArgumentValidationOutcome

    object InvalidArgumentError : ArgumentValidationOutcome, SemanticError {
        override fun toString(): String = "InvalidArgumentError"
    }

    private val subjectCallOutcome: Computation<SubjectCallOutcome> by lazy {
        subject.inferredType.thenJust { subjectType ->
            when (subjectType) {
                is FunctionType -> LegalSubjectCallResult(
                    calleeType = subjectType,
                )

                else -> IllegalSubjectCallError(
                    illegalSubjectType = subjectType,
                )
            }
        }
    }

    private val argumentValidationOutcome: Computation<ArgumentValidationOutcome?> = Computation.combine2(
        subjectCallOutcome,
        argument.inferredType,
    ) {
            subjectCallOutcome,
            argumentType,
        ->

        when (subjectCallOutcome) {
            is LegalSubjectCallResult -> {
                val subjectType = subjectCallOutcome.calleeType

                if (argumentType == subjectType.argumentType) {
                    ValidArgumentResult
                } else {
                    InvalidArgumentError
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
}

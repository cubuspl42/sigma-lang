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

    sealed interface SubjectCall {
        class Function(
            val calleeType: FunctionType,
        ) : SubjectCall {
            val errors: Set<SemanticError> = emptySet()
        }

        class Illegal(
            val illegalSubjectType: Type,
        ) : SubjectCall, SemanticError
    }

    private val subjectCall: Computation<SubjectCall> by lazy {
        subject.inferredType.thenJust { subjectType ->
            if (subjectType is FunctionType) {
                return@thenJust SubjectCall.Function(calleeType = subjectType)
            } else {
                return@thenJust SubjectCall.Illegal(illegalSubjectType = subjectType)
            }
        }
    }

    override val inferredType: Computation<Type> by lazy {
        subjectCall.thenJust { subjectCall ->
            if (subjectCall is SubjectCall.Function) {
                subjectCall.calleeType.imageType
            } else {
                IllType
            }
        }
    }

    override val errors: Set<SemanticError> by lazy {
        when (val it = subjectCall.value) {
            is SubjectCall.Function -> it.errors
            is SubjectCall.Illegal -> setOf(it)
            null -> emptySet()
        }
    }
}

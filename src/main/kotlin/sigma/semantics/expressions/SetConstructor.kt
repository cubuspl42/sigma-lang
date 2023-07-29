package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.SetValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateInitialValue
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.IllType
import sigma.semantics.types.SetType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.SetConstructorTerm

class SetConstructor(
    override val term: SetConstructorTerm,
    val elements: Set<Expression>,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: SetConstructorTerm,
        ): SetConstructor = SetConstructor(
            term = term,
            elements = term.elements.map {
                Expression.build(
                    declarationScope = declarationScope,
                    term = it,
                )
            }.toSet(),
        )
    }

    sealed interface InferredElementTypeOutcome

    data class InferredElementTypeResult(
        val elementType: Type,
    ) : InferredElementTypeOutcome

    data class InconsistentElementTypeError(
        override val location: SourceLocation,
    ) : InferredElementTypeOutcome, SemanticError

    private val inferredElementTypeOutcome: Thunk<InferredElementTypeOutcome> = Thunk.traverseList(
        elements.toList()
    ) {
        it.inferredType
    }.thenJust { elementTypes ->
        val distinctiveElementTypes = elementTypes.toSet()

        val elementType = distinctiveElementTypes.singleOrNull()

        if (elementType != null) {
            InferredElementTypeResult(
                elementType = elementType,
            )
        } else {
            InconsistentElementTypeError(
                location = term.location,
            )
        }
    }

    override val inferredType: Thunk<Type> = inferredElementTypeOutcome.thenJust { inferredValueTypeOutcome ->
        if (inferredValueTypeOutcome is InferredElementTypeResult) {
            SetType(
                elementType = inferredValueTypeOutcome.elementType,
            )
        } else {
            IllType
        }
    }

    override fun bind(scope: Scope): Thunk<Value> = SetValue(
        elements = elements.map {
            it.bind(
                scope = scope,
            ).evaluateInitialValue()
        }.toSet(),
    ).asThunk

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredElementTypeOutcome.value as? InconsistentElementTypeError,
        )
    }
}

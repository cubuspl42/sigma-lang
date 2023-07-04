package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.SetValue
import sigma.semantics.Computation
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.TypeScope
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
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: SetConstructorTerm,
        ): SetConstructor = SetConstructor(
            term = term,
            elements = term.elements.map {
                Expression.build(
                    typeScope = typeScope,
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

    private val inferredElementTypeOutcome: Computation<InferredElementTypeOutcome> = Computation.traverseList(
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

    override val inferredType: Computation<Type> = inferredElementTypeOutcome.thenJust { inferredValueTypeOutcome ->
        if (inferredValueTypeOutcome is InferredElementTypeResult) {
            SetType(
                elementType = inferredValueTypeOutcome.elementType,
            )
        } else {
            IllType
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredElementTypeOutcome.value as? InconsistentElementTypeError,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): SetValue = SetValue(
        elements = elements.map {
            it.evaluate(scope = scope).toEvaluatedValue
        }.toSet(),
    )
}

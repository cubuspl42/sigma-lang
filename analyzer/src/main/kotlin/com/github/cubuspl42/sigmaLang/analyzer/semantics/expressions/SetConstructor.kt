package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SetConstructorTerm

class SetConstructor(
    override val outerScope: StaticScope,
    override val term: SetConstructorTerm,
    val elements: Set<Expression>,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: SetConstructorTerm,
        ): SetConstructor = SetConstructor(
            outerScope = outerScope,
            term = term,
            elements = term.elements.map {
                Expression.build(
                    outerScope = outerScope,
                    term = it,
                )
            }.toSet(),
        )
    }

    sealed interface InferredElementTypeOutcome

    data class InferredElementTypeResult(
        val elementType: MembershipType,
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

    override val inferredType: Thunk<MembershipType> = inferredElementTypeOutcome.thenJust { inferredValueTypeOutcome ->
        if (inferredValueTypeOutcome is InferredElementTypeResult) {
            SetType(
                elementType = inferredValueTypeOutcome.elementType,
            )
        } else {
            IllType
        }
    }

    override val subExpressions: Set<Expression> = elements

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements.toList()) {
        it.bind(dynamicScope = dynamicScope)
    }.thenJust { elements ->
        SetValue(
            elements = elements.toSet(),
        )
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredElementTypeOutcome.value as? InconsistentElementTypeError,
        )
    }
}

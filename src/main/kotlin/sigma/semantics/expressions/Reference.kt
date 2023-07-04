package sigma.semantics.expressions

import sigma.evaluation.Thunk
import sigma.evaluation.scope.Scope
import sigma.semantics.Computation
import sigma.semantics.Declaration
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ReferenceTerm
import sigma.evaluation.values.Symbol

class Reference(
    private val declarationScope: DeclarationScope,
    override val term: ReferenceTerm,
) : Expression() {
    data class UnresolvedNameError(
        override val location: SourceLocation,
        val name: Symbol,
    ) : SemanticError

    companion object {
        fun build(
            declarationScope: DeclarationScope,
            term: ReferenceTerm,
        ): Reference = Reference(
            declarationScope = declarationScope,
            term = term,
        )
    }

    val referredName = term.referee

    private val referredDeclaration: Declaration? by lazy {
        declarationScope.resolveDeclaration(name = term.referee)
    }

    override val inferredType: Computation<Type> = Computation.lazy {
        when (val it = referredDeclaration) {
            null -> Computation.pure(IllType)
            else -> it.inferredType
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            if (referredDeclaration == null) UnresolvedNameError(
                location = term.location,
                name = term.referee,
            ) else null
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk = scope.getValue(referredName) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )
}

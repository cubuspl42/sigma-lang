package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.EvaluationResult
import sigma.semantics.Computation
import sigma.semantics.ValueDeclaration
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ReferenceTerm
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.semantics.Declaration
import sigma.semantics.ResolvedName

class Reference(
    private val declarationScope: StaticScope,
    override val term: ReferenceTerm,
) : Expression() {
    data class UnresolvedNameError(
        override val location: SourceLocation,
        val name: Symbol,
    ) : SemanticError

    data class NonValueDeclarationError(
        override val location: SourceLocation,
        val name: Symbol,
    ) : SemanticError

    companion object {
        fun build(
            declarationScope: StaticScope,
            term: ReferenceTerm,
        ): Reference = Reference(
            declarationScope = declarationScope,
            term = term,
        )
    }

    val referredName = term.referee

    private val resolved: ResolvedName? by lazy {
        declarationScope.resolveName(name = term.referee)
    }

    override val inferredType: Computation<Type> = Computation.lazy {
        return@lazy Computation.pure(resolved?.type ?: IllType)
    }

    override fun bind(scope: Scope): Thunk<*> = scope.getValue(
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            when (resolved) {
                null -> UnresolvedNameError(
                    location = term.location,
                    name = term.referee,
                )

                else -> null
            }
        )
    }
}

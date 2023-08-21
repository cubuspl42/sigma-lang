package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ReferenceSourceTerm
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.ResolvedName

class Reference(
    private val declarationScope: StaticScope,
    override val term: ReferenceSourceTerm,
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
            term: ReferenceSourceTerm,
        ): Reference = Reference(
            declarationScope = declarationScope,
            term = term,
        )
    }

    val referredName = term.referredName

    private val resolved: ResolvedName? by lazy {
        declarationScope.resolveName(name = term.referredName)
    }

    override val inferredType: Thunk<Type>
        get() = resolved?.type ?: Thunk.pure(IllType)

    override fun bind(scope: Scope): Thunk<Value> = scope.getValue(
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            when (resolved) {
                null -> UnresolvedNameError(
                    location = term.location,
                    name = term.referredName,
                )

                else -> null
            }
        )
    }
}

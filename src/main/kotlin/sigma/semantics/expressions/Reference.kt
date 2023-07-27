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
import sigma.semantics.Declaration

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

    private val referredDeclaration: Declaration? by lazy {
        declarationScope.resolveName(name = term.referee)
    }

    override val inferredType: Computation<Type> = Computation.lazy {
        val referredValueDeclaration = referredDeclaration as? ValueDeclaration

        when (val it = referredValueDeclaration) {
            null -> Computation.pure(IllType)
            else -> it.effectiveValueType
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            when (referredDeclaration) {
                null -> UnresolvedNameError(
                    location = term.location,
                    name = term.referee,
                )
                !is ValueDeclaration -> NonValueDeclarationError(
                    location = term.location,
                    name = term.referee,
                )
                else -> null
            }
        )
    }

    override fun evaluate(
        context: EvaluationContext,
        scope: Scope,
    ): EvaluationResult = scope.getValue(
        context = context,
        name = referredName,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredName",
    )
}

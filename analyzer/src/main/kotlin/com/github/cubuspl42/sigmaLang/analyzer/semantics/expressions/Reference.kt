package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName

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
            outerScope: StaticScope,
            term: ReferenceSourceTerm,
        ): Reference = Reference(
            declarationScope = outerScope,
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

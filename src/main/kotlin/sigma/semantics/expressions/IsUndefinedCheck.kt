package sigma.semantics.expressions

import sigma.semantics.Computation
import sigma.semantics.TypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.syntax.expressions.IsUndefinedCheckTerm

data class IsUndefinedCheck(
    override val term: IsUndefinedCheckTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: IsUndefinedCheckTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            term = term,
            argument = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.argument,
            ),
        )
    }

    override val inferredType: Computation<Type> = Computation.pure(BoolType)

    override val errors: Set<SemanticError> = emptySet()
}

package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.ArrayType
import sigma.semantics.types.Type
import sigma.syntax.expressions.ArrayTypeConstructorSourceTerm
import sigma.syntax.expressions.ExpressionSourceTerm

class ArrayTypeConstructor(
    override val term: ExpressionSourceTerm,
    val elementType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: ArrayTypeConstructorSourceTerm,
        ): ArrayTypeConstructor = ArrayTypeConstructor(
            term = term,
            elementType = Expression.build(
                declarationScope = declarationScope,
                term = term.elementType,
            ),
        )
    }

    override val inferredType: Thunk<Type>
        get() = TODO()

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(scope: Scope): Thunk<Value> = elementType.bind(
        scope = scope,
    ).thenJust {
        ArrayType(elementType = it as Type)
    }
}

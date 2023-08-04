package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.DictType
import sigma.semantics.types.Type
import sigma.syntax.expressions.DictTypeConstructorTerm
import sigma.syntax.expressions.ExpressionTerm

class DictTypeConstructor(
    override val term: ExpressionTerm,
    val keyType: Expression,
    val valueType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: DictTypeConstructorTerm,
        ): DictTypeConstructor = DictTypeConstructor(
            term = term,
            keyType = Expression.build(
                declarationScope = declarationScope,
                term = term.keyType,
            ),
            valueType = Expression.build(
                declarationScope = declarationScope,
                term = term.valueType,
            ),
        )
    }

    override val inferredType: Thunk<Type>
        get() = TODO()

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.combine2(
        keyType.bind(
            scope = scope,
        ), valueType.bind(
            scope = scope,
        )
    ) { keyType, valueType ->
        DictType(
            keyType = keyType as Type,
            valueType = valueType as Type,
        )
    }
}

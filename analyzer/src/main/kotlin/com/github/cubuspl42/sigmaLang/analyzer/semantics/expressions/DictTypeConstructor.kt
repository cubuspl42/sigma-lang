package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm

class DictTypeConstructor(
    override val term: ExpressionSourceTerm,
    val keyType: Expression,
    val valueType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: DictTypeConstructorSourceTerm,
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

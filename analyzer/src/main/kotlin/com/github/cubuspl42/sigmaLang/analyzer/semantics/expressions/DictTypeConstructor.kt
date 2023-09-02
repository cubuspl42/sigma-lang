package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class DictTypeConstructor(
    override val outerScope: StaticScope,
    override val term: ExpressionTerm,
    val keyType: Expression,
    val valueType: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: DictTypeConstructorTerm,
        ): DictTypeConstructor = DictTypeConstructor(
            outerScope = outerScope,
            term = term,
            keyType = Expression.build(
                outerScope = outerScope,
                term = term.keyType,
            ),
            valueType = Expression.build(
                outerScope = outerScope,
                term = term.valueType,
            ),
        )
    }

    override val inferredType: Thunk<Type>
        get() = TODO()

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.combine2(
        keyType.bind(
            dynamicScope = dynamicScope,
        ), valueType.bind(
            dynamicScope = dynamicScope,
        )
    ) { keyType, valueType ->
        DictType(
            keyType = keyType as Type,
            valueType = valueType as Type,
        ).asValue
    }

    override val subExpressions: Set<Expression> = setOf(keyType, valueType)
}

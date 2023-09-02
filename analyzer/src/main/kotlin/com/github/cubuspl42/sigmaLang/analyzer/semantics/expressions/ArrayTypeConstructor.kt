package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class ArrayTypeConstructor(
    override val outerScope: StaticScope,
    override val term: ExpressionTerm,
    val elementType: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ArrayTypeConstructorTerm,
        ): ArrayTypeConstructor = ArrayTypeConstructor(
            outerScope = outerScope,
            term = term,
            elementType = Expression.build(
                outerScope = outerScope,
                term = term.elementType,
            ),
        )
    }

    override val inferredType: Thunk<Type>
        get() = TODO()

    override val subExpressions: Set<Expression> = setOf(elementType)

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = elementType.bind(
        dynamicScope = dynamicScope,
    ).thenJust {
        ArrayType(elementType = it as Type)
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm

class ArrayTypeConstructor(
    override val term: ExpressionSourceTerm,
    val elementType: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ArrayTypeConstructorSourceTerm,
        ): ArrayTypeConstructor = ArrayTypeConstructor(
            term = term,
            elementType = Expression.build(
                outerScope = outerScope,
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

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class ArrayTypeConstructor(
    override val outerScope: StaticScope,
    override val term: ExpressionTerm,
    val elementType: Expression,
) : TypeConstructor() {
    companion object {
        fun build(
            context: BuildContext,
            term: ArrayTypeConstructorTerm,
        ): ArrayTypeConstructor = ArrayTypeConstructor(
            outerScope = context.outerScope,
            term = term,
            elementType = Expression.build(
                context = context,
                term = term.elementType,
            ),
        )
    }

    override val subExpressions: Set<Expression> = setOf(elementType)

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = elementType.bind(
        dynamicScope = dynamicScope,
    ).thenJust {
        ArrayType(elementType = it.asType!!).asValue
    }

    override val classifiedValue: ClassificationContext<Value> =
        elementType.classifiedValue.transform { elementAnalysis ->
            ArrayType(elementType = elementAnalysis.asType!!).asValue
        }
}

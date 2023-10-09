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

abstract class ArrayTypeConstructor : TypeConstructor() {
    companion object {
        fun build(
            context: BuildContext,
            term: ArrayTypeConstructorTerm,
        ): Stub<ArrayTypeConstructor> = object : Stub<ArrayTypeConstructor> {
            override val resolved: ArrayTypeConstructor by lazy {
                object : ArrayTypeConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: ExpressionTerm = term

                    override val elementType: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.elementType,
                        ).resolved
                    }
                }
            }
        }
    }

    abstract val elementType: Expression

    override val subExpressions: Set<Expression> by lazy { setOf(elementType) }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = elementType.bind(
        dynamicScope = dynamicScope,
    ).thenJust {
        ArrayType(elementType = it.asType!!).asValue
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        elementType.classifiedValue.transform { elementAnalysis ->
            ArrayType(elementType = elementAnalysis.asType!!).asValue
        }
    }
}

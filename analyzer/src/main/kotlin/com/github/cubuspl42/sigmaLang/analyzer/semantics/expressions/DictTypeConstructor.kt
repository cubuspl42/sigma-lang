package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class DictTypeConstructor(
    override val outerScope: StaticScope,
    override val term: ExpressionTerm,
    val keyType: Expression,
    val valueType: Expression,
) : TypeConstructor() {
    companion object {
        fun build(
            context: BuildContext,
            term: DictTypeConstructorTerm,
        ): DictTypeConstructor = DictTypeConstructor(
            outerScope = context.outerScope,
            term = term,
            keyType = Expression.build(
                context = context,
                term = term.keyType,
            ),
            valueType = Expression.build(
                context = context,
                term = term.valueType,
            ),
        )
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.transform2(
            keyType.classifiedValue,
            valueType.classifiedValue,
        ) { keyValue, valueValue ->
            Thunk.pure(
                DictType(
                    keyType = keyValue.asType!!,
                    valueType = valueValue.asType!!,
                ).asValue,
            )
        }
    }

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
            keyType = keyType.asType!!,
            valueType = valueType.asType!!,
        ).asValue
    }

    override val subExpressions: Set<Expression> = setOf(keyType, valueType)
}

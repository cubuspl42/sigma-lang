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

abstract class DictTypeConstructor(
) : TypeConstructor() {
    abstract override val term: DictTypeConstructorTerm

    abstract val keyType: Expression

    abstract val valueType: Expression

    companion object {
        fun build(
            context: BuildContext,
            term: DictTypeConstructorTerm,
        ): Stub<DictTypeConstructor> = object : Stub<DictTypeConstructor> {
            override val resolved: DictTypeConstructor by lazy {
                object : DictTypeConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: DictTypeConstructorTerm = term

                    override val keyType: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.keyType,
                        ).resolved
                    }
                    override val valueType: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.valueType,
                        ).resolved
                    }
                }
            }
        }
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

    override val subExpressions: Set<Expression>
        get() = setOf(keyType, valueType)
}

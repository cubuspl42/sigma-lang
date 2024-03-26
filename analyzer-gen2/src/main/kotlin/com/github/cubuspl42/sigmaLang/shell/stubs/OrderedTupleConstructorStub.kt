package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.OrderedTupleConstructor
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class OrderedTupleConstructorStub(
    private val elementStubs: List<ExpressionStub<Expression>>,
) : ExpressionStub<OrderedTupleConstructor>() {
    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<OrderedTupleConstructor> = OrderedTupleConstructor.builder(
        elements = elementStubs.map {
            it.transform(context = context)
        },
    )
}

package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType

class ComputableAbstraction(
    private val outerDynamicScope: DynamicScope,
    private val argumentType: TupleType,
    private val image: Expression,
) : Abstraction() {
    override fun apply(
        argument: Value,
    ): Thunk<Value> = image.bind(
        dynamicScope = argumentType.toArgumentScope(
            argument = argument as DictValue,
        ).chainWith(
            context = outerDynamicScope,
        ),
    )
}

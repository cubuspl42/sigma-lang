package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType

object SetSum : StrictBuiltinOrderedFunctionConstructor() {
    override val argumentElements: List<OrderedTupleType.Element> = listOf(
        OrderedTupleType.Element(
            name = null,
            type = SetType(
                elementType = IntCollectiveType,
            ),
        ),
    )

    override val imageType: SpecificType = IntCollectiveType

    override fun compute(args: List<Value>): Value {
        val arg = args[0] as SetValue
        return IntValue(value = arg.elements.sumOf { (it as IntValue).value })
    }
}

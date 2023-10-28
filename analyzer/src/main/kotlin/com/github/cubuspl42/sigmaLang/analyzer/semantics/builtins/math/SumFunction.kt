package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.math

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType

object SumFunction: StrictBuiltinOrderedFunctionConstructor() {
    override val argumentElements: List<OrderedTupleType.Element> = listOf(
        OrderedTupleType.Element(
            name = Identifier.of("elements"),
            type = ArrayType(
                elementType = IntCollectiveType,
            ),
        ),
    )

    override val imageType = IntCollectiveType

    override fun compute(args: List<Value>): Value {
        val elements = (args[0] as FunctionValue).toList()

        return IntValue(
            value = elements.sumOf { (it as IntValue).value },
        )
    }
}

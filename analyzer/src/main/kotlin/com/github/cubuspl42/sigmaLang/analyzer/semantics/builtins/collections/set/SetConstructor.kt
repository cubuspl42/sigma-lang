package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue

object SetConstructor : StrictBuiltinOrderedFunctionConstructor() {
    override fun compute(args: List<Value>): Value {
        val elementType = args[0].asType!!

        return SetType(
            elementType = elementType,
        ).asValue
    }

    override val argumentElements: List<OrderedTupleType.Element> = listOf(
        OrderedTupleType.Element(
            name = Identifier.of("elementType"),
            type = TypeType,
        ),
    )

    override val imageType: SpecificType = TypeType
}

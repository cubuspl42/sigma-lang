package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object ConcatFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = GenericType.orderedTraitDeclaration(
        Identifier.of("e"),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    override val body = object : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("front"),
                type = ArrayType(
                    elementType = eTypeVariable,
                ),
            ),
            OrderedTupleType.Element(
                name = Identifier.of("back"),
                type = ArrayType(
                    elementType = eTypeVariable,
                ),
            ),
        )

        override val imageType = ArrayType(
            elementType = eTypeVariable,
        )

        override fun compute(args: List<Value>): Value {
            val front = (args[0] as FunctionValue).toList()
            val back = (args[1] as FunctionValue).toList()

            return DictValue.fromList(front + back)
        }
    }
}

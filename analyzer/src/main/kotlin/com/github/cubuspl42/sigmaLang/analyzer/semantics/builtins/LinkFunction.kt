package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object LinkFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = orderedTraitDeclaration(
        Identifier.of("e"),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    override val body = object : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("primary"),
                type = DictType(
                    keyType = eTypeVariable,
                    valueType = eTypeVariable,
                ),
            ),
            OrderedTupleType.Element(
                name = Identifier.of("secondary"),
                type = DictType(
                    keyType = eTypeVariable,
                    valueType = eTypeVariable,
                ),
            ),
        )

        override val imageType = DictType(
            keyType = eTypeVariable,
            valueType = eTypeVariable,
        )

        override fun compute(args: List<Value>): Value {
            val primary = (args.first() as FunctionValue).toList()
            val secondary = (args[1] as FunctionValue).toList()

            val result = primary + secondary

            return DictValue.fromList(result)
        }
    }
}

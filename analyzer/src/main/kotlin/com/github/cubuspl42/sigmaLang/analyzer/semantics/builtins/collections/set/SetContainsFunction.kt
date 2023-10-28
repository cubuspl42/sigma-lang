package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object SetContainsFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = ArgumentDeclaration(
        declaredType = OrderedTupleType(
            elements = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("e"),
                    type = TypeType,
                ),
            ),
        ),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    override val body = object : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("set"),
                type = SetType(
                    elementType = eTypeVariable,
                ),
            ),
            OrderedTupleType.Element(
                name = Identifier.of("element"),
                type = eTypeVariable,
            ),
        )

        override val imageType = BoolType

        override fun compute(args: List<Value>): Value {
            val set = (args[0] as SetValue).elements
            val element = args[1]

            return BoolValue(
                value = set.contains(element),
            )
        }
    }
}

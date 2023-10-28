package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object IfFunction : BuiltinGenericFunctionConstructor() {
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
                name = Identifier.of("guard"),
                type = BoolType,
            ),
            OrderedTupleType.Element(
                name = Identifier.of("then"),
                type = eTypeVariable,
            ),
            OrderedTupleType.Element(
                name = Identifier.of("else"),
                type = eTypeVariable,
            ),
        )

        override val imageType = eTypeVariable

        override fun compute(args: List<Value>): Value {
            val guard = (args[0] as BoolValue).value
            val then = args[1]
            val `else` = args[2]

            return if (guard) then else `else`
        }
    }
}

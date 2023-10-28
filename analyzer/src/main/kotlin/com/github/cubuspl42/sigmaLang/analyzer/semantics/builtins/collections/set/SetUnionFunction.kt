package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object SetUnionFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = orderedTraitDeclaration(
        Identifier.of("e"),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    override val body = object : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("set0"),
                type = SetType(
                    elementType = eTypeVariable,
                ),
            ),
            OrderedTupleType.Element(
                name = Identifier.of("set1"),
                type = SetType(
                    elementType = eTypeVariable,
                ),
            ),
        )

        override val imageType = SetType(
            elementType = eTypeVariable,
        )

        override fun compute(args: List<Value>): Value {
            val set0 = (args[0] as SetValue).elements
            val set1 = (args[1] as SetValue).elements

            return SetValue(
                elements = set0 + set1,
            )
        }
    }
}

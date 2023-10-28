package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable


object SetOfFunction : BuiltinGenericFunctionConstructor() {
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
                name = Identifier.of("elements"),
                type = ArrayType(
                    elementType = eTypeVariable,
                ),
            ),
        )

        override val imageType = SetType(
            elementType = eTypeVariable,
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            return SetValue(
                elements = elements.toSet(),
            )
        }
    }
}

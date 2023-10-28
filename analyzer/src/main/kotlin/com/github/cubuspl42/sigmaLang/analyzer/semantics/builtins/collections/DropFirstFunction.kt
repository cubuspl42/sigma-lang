package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object DropFirstFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = GenericType.orderedTraitDeclaration(
        Identifier.of("e"),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    override val body: BuiltinFunctionConstructor = object : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("elements"),
                type = ArrayType(
                    elementType = eTypeVariable,
                ),
            ),
            OrderedTupleType.Element(
                name = Identifier.of("n"),
                type = IntCollectiveType,
            ),
        )

        override val imageType = ArrayType(
            elementType = eTypeVariable,
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.drop(1)

            return DictValue.fromList(result)
        }
    }
}

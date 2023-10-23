package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

object SetUnionFunction : BuiltinValue {
    private val elementTypeDefinition = TypeVariableDefinition()

    override val type: SpecificType = UniversalFunctionType(
        argumentType = OrderedTupleType.of(
            SetType(elementType = elementTypeDefinition.typePlaceholder),
            SetType(elementType = elementTypeDefinition.typePlaceholder),
        ),
        imageType = SetType(
            elementType = elementTypeDefinition.typePlaceholder,
        ),
    )

    override val value: Value = object : FunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val args = (argument as FunctionValue).toList()

            val set0 = (args[0] as SetValue).elements
            val set1 = (args[1] as SetValue).elements

            return SetValue(
                elements = set0 + set1,
            ).toThunk()
        }

        override fun dump(): String = "(setUnion)"
    }
}

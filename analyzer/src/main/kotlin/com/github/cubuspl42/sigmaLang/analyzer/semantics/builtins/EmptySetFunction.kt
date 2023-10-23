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

object EmptySetFunction : BuiltinValue {
    private val elementTypeDefinition = TypeVariableDefinition()

    override val type: SpecificType = UniversalFunctionType(
        argumentType = OrderedTupleType.Empty,
        imageType = SetType(
            elementType = elementTypeDefinition.typePlaceholder,
        ),
    )

    override val value: Value = object : FunctionValue() {
        override fun apply(argument: Value): Thunk<Value> = SetValue(
            elements = emptySet(),
        ).toThunk()

        override fun dump(): String = "(emptySet)"
    }
}

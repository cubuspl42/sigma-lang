package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

object SetContainsFunction : BuiltinValue {
    private val elementTypeDefinition = TypeVariableDefinition()

    override val type: SpecificType = UniversalFunctionType(
        argumentType = OrderedTupleType.of(
            SetType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
            elementTypeDefinition.typePlaceholder,
        ),
        imageType = BoolType,
    )

    override val value: Value = object : FunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val args = (argument as FunctionValue).toList()

            val set = args[0] as SetValue
            val element = args[1]

            return BoolValue(
                value = set.elements.contains(element),
            ).toThunk()
        }

        override fun dump(): String = "(setContains)"
    }
}
package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.SetValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType

object EmptySetFunction : BuiltinValue {
    private val elementTypeDefinition = TypeVariableDefinition(
        name = Identifier.of("elementType"),
    )

    override val type: MembershipType = UniversalFunctionType(
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

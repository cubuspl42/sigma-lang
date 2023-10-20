package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType

object IfFunction : BuiltinValue {
    private val rDefinition = TypeVariableDefinition(
        name = Identifier.of("R"),
    )

    override val type: MembershipType = UniversalFunctionType(
        argumentType = OrderedTupleType(
            elements = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("guard"),
                    type = BoolType,
                ),
            ),
        ), imageType = UniversalFunctionType(
            argumentType = UnorderedTupleType(
                valueTypeByName = mapOf(
                    Identifier.of("then") to rDefinition.typePlaceholder,
                    Identifier.of("else") to rDefinition.typePlaceholder,
                )
            ),
            imageType = rDefinition.typePlaceholder,
        )
    )

    override val value: Value
        get() = BoolValue.If
}

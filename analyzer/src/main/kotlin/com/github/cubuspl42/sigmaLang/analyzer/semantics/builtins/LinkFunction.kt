package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType

object LinkFunction : BuiltinValue {
    private val kDefinition = TypeVariableDefinition()

    private val vDefinition = TypeVariableDefinition()

    override val type: SpecificType = UniversalFunctionType(
        argumentType = UnorderedTupleType(
            valueTypeByName = mapOf(
                Identifier.of("primary") to DictType(
                    keyType = kDefinition.typePlaceholder,
                    valueType = vDefinition.typePlaceholder,
                ),
                Identifier.of("secondary") to DictType(
                    keyType = kDefinition.typePlaceholder,
                    valueType = vDefinition.typePlaceholder,
                ),
            )
        ),
        imageType = DictType(
            keyType = kDefinition.typePlaceholder,
            valueType = vDefinition.typePlaceholder,
        ),
    )
    override val value: Value
        get() = FunctionValue.Link
}
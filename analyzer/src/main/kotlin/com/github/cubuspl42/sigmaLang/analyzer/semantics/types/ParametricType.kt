package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue

abstract class ParametricType : Type() {
    abstract val parameterType: TupleType

    val functionType: UniversalFunctionType
        get() = UniversalFunctionType(
            argumentType = parameterType,
            imageType = TypeType,
        )

    final override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    final override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun dumpDirectly(depth: Int): String = "(parametrized type)"

    // Thought: Return a thunk?
    abstract fun parametrize(
        metaArgument: DictValue,
    ): Type

    override fun specifyImplicitly(): Type = parametrize(
        metaArgument = DictValue.Empty,
    )
}

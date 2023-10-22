package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue

data class GenericType(
    val metaArgumentType: TupleType,
) : Type() {
    val metaType: UniversalFunctionType
        get() = UniversalFunctionType(
            argumentType = metaArgumentType,
            imageType = TypeType,
        )

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun dumpDirectly(depth: Int): String = "${metaArgumentType.dumpRecursively(depth)} !-> Type"

    fun specify(
        argument: DictValue,
    ): Type {
        TODO()
    }
}

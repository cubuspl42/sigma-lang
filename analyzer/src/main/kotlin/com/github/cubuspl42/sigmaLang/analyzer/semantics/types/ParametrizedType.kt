package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue

abstract class ParametrizedType : Type() {
    abstract val parameterType: TupleType

    final override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    final override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    final override fun dumpDirectly(depth: Int): String = "${parameterType.dumpRecursively(depth)} !-> Type"

    // Thought: Return a thunk?
    abstract fun parametrize(
        metaArgument: DictValue,
    ): Type
}

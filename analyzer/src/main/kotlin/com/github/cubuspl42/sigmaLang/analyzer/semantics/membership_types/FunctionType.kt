package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula

sealed class FunctionType : ShapeType() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    abstract override fun substituteTypeVariables(resolution: TypeVariableResolution): FunctionType

    open val metaArgumentType: TupleType? = null

    val typeVariables: Set<TypeVariable>
        get() = metaArgumentType?.typeVariableDefinitions?.let { definitions ->
            definitions.map {
                TypeVariable(
                    formula = Formula(
                        name = it.name,
                    )
                )
            }.toSet()
        } ?: emptySet()

    abstract val argumentType: MembershipType

    abstract val imageType: MembershipType
}

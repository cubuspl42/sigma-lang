package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope.SimpleBuiltinValue

// Type of sets
data class SetType(
    val elementType: MembershipType,
) : ShapeType() {
    companion object {
        val constructor = SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    MetaType,
                ),
                imageType = MetaType,
            ),
            value = object : FunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val args = (argument as FunctionValue).toList()

                    val elementType = args[0].asType!!

                    return SetType(
                        elementType = elementType,
                    ).asValue.toThunk()
                }

                override fun dump(): String = "(set type constructor)"
            },
        )

        val setOf = SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    ArrayType(
                        elementType = TypeVariable.of("elementType"),
                    ),
                ),
                imageType = SetType(
                    elementType = TypeVariable.of("elementType"),
                ),
            ),
            value = object : FunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val args = (argument as FunctionValue).toList()

                    val elements = (args[0] as FunctionValue).toList()

                    return SetValue(
                        elements = elements.toSet(),
                    ).toThunk()
                }

                override fun dump(): String = "(setOf)"
            },
        )

        val setUnion = SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    SetType(
                        elementType = TypeVariable.of("elementType"),
                    ),
                    SetType(
                        elementType = TypeVariable.of("elementType"),
                    ),
                ),
                imageType = SetType(
                    elementType = TypeVariable.of("elementType"),
                ),
            ),
            value = object : FunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val args = (argument as FunctionValue).toList()

                    val set0 = (args[0] as SetValue).elements
                    val set1 = (args[1] as SetValue).elements

                    return SetValue(
                        elements = set0 + set1,
                    ).toThunk()
                }

                override fun dump(): String = "(setUnion)"
            },
        )

        val setContains = SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType.of(
                    SetType(
                        elementType = TypeVariable.of("elementType"),
                    ),
                    TypeVariable.of("elementType"),
                ),
                imageType = BoolType,
            ),
            value = object : FunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val args = (argument as FunctionValue).toList()

                    val set = args[0] as SetValue
                    val element = args[1]

                    return BoolValue(
                        value = set.elements.contains(element),
                    ).toThunk()
                }

                override fun dump(): String = "(setContains)"
            },
        )

        val emptySet = SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType.Empty,
                imageType = SetType(
                    elementType = TypeVariable.of("elementType"),
                ),
            ),
            value = object : FunctionValue() {
                override fun apply(argument: Value): Thunk<Value> = SetValue(
                    elements = emptySet(),
                ).toThunk()

                override fun dump(): String = "(emptySet)"
            },
        )
    }

    object SetSum : StrictBuiltinOrderedFunction() {
        override val argTypes: List<MembershipType> = listOf(
            SetType(
                elementType = IntCollectiveType,
            ),
        )

        override val imageType: MembershipType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val arg = args[0] as SetValue
            return IntValue(value = arg.elements.sumOf { (it as IntValue).value })
        }
    }

    data class SetMatch(
        val elementMatch: MembershipType.MatchResult,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = when {
            !elementMatch.isFull() -> "set element type: " + elementMatch.dump()
            else -> "(?)"
        }
    }

    override fun dump(): String = "{${elementType.dump()}*}"

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = when (other) {
        is SetType -> SetType(
            elementType = elementType.findLowestCommonSupertype(other.elementType),
        )

        else -> AnyType
    }

    override fun resolveTypeVariablesShape(
        assignedType: MembershipType,
    ): TypeVariableResolution {
        if (assignedType !is SetType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-set is assigned",
        )

        val elementResolution = elementType.resolveTypeVariables(
            assignedType = assignedType.elementType,
        )

        return elementResolution
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): SetType = SetType(
        elementType = elementType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (val sealedAssignedType = assignedType) {
        is SetType -> SetMatch(
            elementMatch = elementType.match(
                assignedType = sealedAssignedType.elementType,
            ),
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = sealedAssignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = elementType.walk()
}
package sigma.semantics.types

import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.ComputableFunctionValue
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.SetValue
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.semantics.BuiltinScope
import sigma.semantics.BuiltinScope.SimpleBuiltinValue

// Type of sets
data class SetType(
    val elementType: Type,
) : Type() {
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

                    val elementType = args[0] as Type

                    return SetType(
                        elementType = elementType,
                    ).asThunk
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
                    ).asThunk
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
                    ).asThunk
                }

                override fun dump(): String = "(setOf)"
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
                    ).asThunk
                }

                override fun dump(): String = "(setOf)"
            },
        )
    }

    data class SetMatch(
        val elementMatch: Type.MatchResult,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = when {
            !elementMatch.isFull() -> "elementMatch: " + elementMatch.dump()
            else -> "(?)"
        }
    }

    override fun dump(): String = "{${elementType.dump()}*}"

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is SetType -> SetType(
            elementType = elementType.findLowestCommonSupertype(other.elementType),
        )

        else -> AnyType
    }

    override fun resolveTypeVariables(
        assignedType: Type,
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

    override fun match(
        assignedType: Type,
    ): MatchResult = when (assignedType) {
        is SetType -> SetMatch(
            elementMatch = assignedType.elementType.match(
                assignedType = elementType,
            ),
        )

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }
}

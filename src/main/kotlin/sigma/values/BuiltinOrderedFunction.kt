package sigma.values

import sigma.BuiltinValue
import sigma.Thunk
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type

abstract class BuiltinOrderedFunction : FunctionValue(), BuiltinValue {
    final override fun dump(): String = "(builtin ordered function)"

    final override fun apply(argument: Value): Thunk {
        val args = argument as FunctionValue

        return compute(
            args = args.toList(),
        )
    }

    final override val type: Type
        get() = UniversalFunctionType(
            argumentType = OrderedTupleType(
                elements = argTypes.map {
                    OrderedTupleType.Element(
                        name = null,
                        type = it,
                    )
                },
            ),
            imageType = imageType,
        )

    final override val value: Value
        get() = this

    // Thought: allow for names
    abstract val argTypes: List<Type>

    abstract val imageType: Type

    override fun equalsTo(other: Value): Boolean = this == other

    abstract fun compute(args: List<Thunk>): Thunk
}

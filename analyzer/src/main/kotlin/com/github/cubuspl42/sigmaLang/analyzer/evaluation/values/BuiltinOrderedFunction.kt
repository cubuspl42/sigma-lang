package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

abstract class BuiltinOrderedFunction : FunctionValue(), BuiltinValue {
    final override fun dump(): String = "(builtin ordered function)"

    final override fun apply(
        argument: Value,
    ): Thunk<Value> {
        val args = argument as FunctionValue

        return computeThunk(
            args = args.toList(),
        )
    }

    final override val type: MembershipType
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
    abstract val argTypes: List<MembershipType>

    abstract val imageType: MembershipType

    abstract fun computeThunk(
        args: List<Value>,
    ): Thunk<Value>
}

abstract class StrictBuiltinOrderedFunction : BuiltinOrderedFunction() {
    override fun computeThunk(
        args: List<Value>,
    ): Thunk<Value> = Thunk.lazy {
        Thunk.pure(compute(args))
    }

    abstract fun compute(
        args: List<Value>,
    ): Value
}

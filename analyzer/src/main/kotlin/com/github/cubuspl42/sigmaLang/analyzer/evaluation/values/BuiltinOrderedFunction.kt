package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType

abstract class BuiltinOrderedFunction : BuiltinFunction() {
    final override fun dump(): String = "(builtin ordered function)"

    final override fun apply(
        argument: Value,
    ): Thunk<Value> {
        val args = argument as FunctionValue

        return computeThunk(
            args = args.toList(),
        )
    }

    final override val argumentType: TableType
        get() = OrderedTupleType(
            elements = argTypes.map {
                OrderedTupleType.Element(
                    name = null,
                    type = it,
                )
            },
        )

    // Thought: allow for names
    abstract val argTypes: List<MembershipType>

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

package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

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

    open fun computeThunk(
        args: List<Value>,
    ): Thunk<Value> = Thunk.lazy {
        Thunk.pure(compute(args))
    }

    open fun compute(
        args: List<Value>,
    ): Value {
        throw NotImplementedError()
    }
}
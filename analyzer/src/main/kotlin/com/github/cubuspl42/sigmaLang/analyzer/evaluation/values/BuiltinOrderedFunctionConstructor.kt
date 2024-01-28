package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

abstract class BuiltinOrderedFunctionConstructor : BuiltinFunctionConstructor() {
    override val function: FunctionValue = object : FunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val args = (argument as FunctionValue).toList()

            return computeThunk(
                args = args,
            )
        }

        override fun dump(): String = "(builtin ordered function)"
    }

    final override val argumentType: TableType by lazy {
        OrderedTupleType(
            elements = argumentElements,
        )
    }

    abstract val argumentElements: List<OrderedTupleType.Element>

    abstract fun computeThunk(
        args: List<Value>,
    ): Thunk<Value>
}

abstract class BuiltinMethodExtractor : BuiltinOrderedFunctionConstructor() {
    final override val argumentElements: List<OrderedTupleType.Element> by lazy {
        listOf(
            OrderedTupleType.Element(
                name = Identifier.of("this"),
                type = selfType,
            ),
        )
    }

    final override val imageType: SpecificType by lazy {
        UniversalFunctionType(
            argumentType = methodArgumentType,
            imageType = methodImageType,
        )
    }


    final override fun computeThunk(args: List<Value>): Thunk<Value> = computeMethodThunk(self = args.first())

    abstract val selfType: SpecificType

    abstract val methodArgumentType: TableType

    abstract val methodImageType: SpecificType

    abstract fun computeMethodThunk(self: Value): Thunk<FunctionValue>
}

abstract class StrictBuiltinOrderedFunctionConstructor : BuiltinOrderedFunctionConstructor() {
    final override fun computeThunk(
        args: List<Value>,
    ): Thunk<Value> = Thunk.lazy {
        Thunk.pure(compute(args))
    }

    abstract fun compute(
        args: List<Value>,
    ): Value
}

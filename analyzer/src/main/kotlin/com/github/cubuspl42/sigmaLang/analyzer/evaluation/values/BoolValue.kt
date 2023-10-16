package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

data class BoolValue(
    val value: Boolean,
) : PrimitiveValue() {
    companion object {
        val False = BoolValue(false)

        val True = BoolValue(true)
    }

    object If : ComputableFunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val test = (argument as DictValue).readValue(IntValue.Zero)!! as BoolValue

            return object : ComputableFunctionValue() {
                override fun apply(argument: Value): Thunk<Value> {
                    val branches = argument as FunctionValue

                    return when {
                        test.value -> branches.apply(Identifier.of("then"))
                        else -> branches.apply(Identifier.of("else"))
                    }
                }

                override fun dump(): String = "(if')"
            }.toThunk()
        }

        override fun dump(): String = "(if)"
    }

    object Not : StrictBuiltinOrderedFunction() {
        override val argTypes: List<MembershipType> = listOf(BoolType)

        override val imageType: MembershipType = BoolType

        override fun compute(args: List<Value>): Value {
            val arg = args[0] as BoolValue
            return BoolValue(value = !arg.value)
        }
    }

    override fun dump(): String = value.toString()
}

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

val BuiltinScope = UnorderedTuple(
    valueByKey = mapOf(
        Identifier("if") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    val conditionValue = args.get(identifier = Identifier(name = "condition")) as BooleanPrimitive

                    return if (conditionValue.isTrue()) {
                        args.get(identifier = Identifier(name = "then"))
                    } else {
                        args.get(identifier = Identifier(name = "else"))
                    }
                }
            },
        ),
    ),
)

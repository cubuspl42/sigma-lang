package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

val BuiltinScope = UnorderedTuple(
    valueByKey = mapOf(
        Identifier("if") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    // TODO: Implement booleans!
                    val conditionValue = args.get(identifier = Identifier(name = "condition"))
                    val thenValue = args.get(identifier = Identifier(name = "then"))
                    val elseValue = args.get(identifier = Identifier(name = "else"))

                    return elseValue
                }
            },
        ),
    ),
)

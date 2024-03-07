package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.ClassBuilder
import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Callable
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
        Identifier("unionWith") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    val firstTuple = args.get(identifier = Identifier(name = "first")) as UnorderedTuple
                    val secondTuple = args.get(identifier = Identifier(name = "second")) as UnorderedTuple

                    return firstTuple.unionWith(secondTuple)
                }
            },
        ),
        Identifier("isA") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    val instanceValue = args.get(identifier = Identifier(name = "instance"))
                    val classValue = args.get(identifier = Identifier(name = "class"))

                    val instancePrototypeValue = (instanceValue as Callable).call(
                        argument = ClassBuilder.instancePrototypeIdentifier,
                    )

                    val classPrototypeValue = (classValue as Callable).call(
                        argument = ClassBuilder.classPrototypeIdentifier,
                    )

                    val instancePrototypeTag = (instancePrototypeValue as Callable).call(
                        argument = ClassBuilder.classTagIdentifier,
                    )

                    val classPrototypeTag = (classPrototypeValue as Callable).call(
                        argument = ClassBuilder.classTagIdentifier,
                    )

                    return BooleanPrimitive(
                        value = instancePrototypeTag == classPrototypeTag,
                    )
                }
            },
        ),
    ),
)

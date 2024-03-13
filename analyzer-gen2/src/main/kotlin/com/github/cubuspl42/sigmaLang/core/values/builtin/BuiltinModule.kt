package com.github.cubuspl42.sigmaLang.core.values.builtin

import com.github.cubuspl42.sigmaLang.core.ClassExpression
import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Indexable
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

object BuiltinModule : Indexable() {
    override val valueByKey = mapOf(
        Identifier.of("Class") to lazyOf(ClassModule),
        Identifier("List") to lazyOf(
            UnorderedTuple(
                valueByKey = mapOf(
                    Identifier.of("of") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as ListValue

                                return args
                            }
                        },
                    ),
                    Identifier.of("concat") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val left = args.get(key = Identifier.of("left")) as ListValue
                                val right = args.get(key = Identifier.of("right")) as ListValue

                                return ListValue(
                                    values = left.values + right.values,
                                )
                            }
                        },
                    ),
                    Identifier.of("head") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val list = args.get(Identifier.of("this")) as ListValue

                                return list.values.first()
                            }
                        },
                    ),
                    Identifier.of("tail") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val list = args.get(Identifier.of("this")) as ListValue

                                return ListValue(
                                    values = list.values.drop(1),
                                )
                            }
                        },
                    ),
                    Identifier.of("isNotEmpty") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val list = args.get(Identifier.of("this")) as ListValue

                                return BooleanPrimitive(
                                    value = list.values.isNotEmpty(),
                                )
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("String") to lazyOf(
            UnorderedTuple(
                valueByKey = mapOf(
                    Identifier.of("concat") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val left = args.get(key = Identifier.of("left")) as StringPrimitive
                                val right = args.get(key = Identifier.of("right")) as StringPrimitive

                                return StringPrimitive(
                                    value = left.value + right.value,
                                )
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("Dict") to lazyOf(
            UnorderedTuple(
                valueByKey = mapOf(
                    Identifier("unionWith") to lazyOf(
                        object : Abstraction() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTuple

                                val firstTuple = args.get(key = Identifier(name = "first")) as UnorderedTuple
                                val secondTuple = args.get(key = Identifier(name = "second")) as UnorderedTuple

                                return firstTuple.unionWith(secondTuple)
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("if") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    val conditionValue = args.get(key = Identifier(name = "condition")) as BooleanPrimitive

                    return if (conditionValue.isTrue()) {
                        args.get(key = Identifier(name = "then"))
                    } else {
                        args.get(key = Identifier(name = "else"))
                    }
                }
            },
        ),
        Identifier("isA") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTuple

                    val instanceValue = args.get(key = Identifier(name = "instance"))
                    val classValue = args.get(key = Identifier(name = "class"))

                    val instancePrototypeValue = (instanceValue as Callable).call(
                        argument = ClassExpression.instancePrototypeIdentifier,
                    )

                    val classPrototypeValue = (classValue as Callable).call(
                        argument = ClassExpression.classPrototypeIdentifier,
                    )

                    val instancePrototypeTag = (instancePrototypeValue as Callable).call(
                        argument = ClassExpression.classTagIdentifier,
                    )

                    val classPrototypeTag = (classPrototypeValue as Callable).call(
                        argument = ClassExpression.classTagIdentifier,
                    )

                    return BooleanPrimitive(
                        value = instancePrototypeTag == classPrototypeTag,
                    )
                }
            },
        ),
        Identifier("panic") to lazyOf(
            object : Abstraction() {
                override fun compute(argument: Value): Value {
                    throw RuntimeException("Panic!")
                }
            },
        ),
    )
}

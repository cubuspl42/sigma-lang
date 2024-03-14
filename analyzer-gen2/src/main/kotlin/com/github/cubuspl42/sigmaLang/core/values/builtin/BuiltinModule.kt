package com.github.cubuspl42.sigmaLang.core.values.builtin

import com.github.cubuspl42.sigmaLang.core.values.AbstractionValue
import com.github.cubuspl42.sigmaLang.core.values.BooleanValue
import com.github.cubuspl42.sigmaLang.core.values.CallableValue
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.IndexableValue
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.StringValue
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value

object BuiltinModule : IndexableValue() {
    override val valueByKey = mapOf(
        Identifier.of("Class") to lazyOf(ClassModule),
        Identifier("List") to lazyOf(
            UnorderedTupleValue(
                valueByKey = mapOf(
                    Identifier.of("of") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as ListValue

                                return args
                            }
                        },
                    ),
                    Identifier.of("concat") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val left = args.get(key = Identifier.of("left")) as ListValue
                                val right = args.get(key = Identifier.of("right")) as ListValue

                                return ListValue(
                                    values = left.values + right.values,
                                )
                            }
                        },
                    ),
                    Identifier.of("head") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val list = args.get(Identifier.of("this")) as ListValue

                                return list.values.first()
                            }
                        },
                    ),
                    Identifier.of("tail") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val list = args.get(Identifier.of("this")) as ListValue

                                return ListValue(
                                    values = list.values.drop(1),
                                )
                            }
                        },
                    ),
                    Identifier.of("isNotEmpty") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val list = args.get(Identifier.of("this")) as ListValue

                                return BooleanValue(
                                    value = list.values.isNotEmpty(),
                                )
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("String") to lazyOf(
            UnorderedTupleValue(
                valueByKey = mapOf(
                    Identifier.of("concat") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val left = args.get(key = Identifier.of("left")) as StringValue
                                val right = args.get(key = Identifier.of("right")) as StringValue

                                return StringValue(
                                    value = left.value + right.value,
                                )
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("Dict") to lazyOf(
            UnorderedTupleValue(
                valueByKey = mapOf(
                    Identifier("unionWith") to lazyOf(
                        object : AbstractionValue() {
                            override fun compute(argument: Value): Value {
                                val args = argument as UnorderedTupleValue

                                val firstTuple = args.get(key = Identifier(name = "first")) as UnorderedTupleValue
                                val secondTuple = args.get(key = Identifier(name = "second")) as UnorderedTupleValue

                                return firstTuple.unionWith(secondTuple)
                            }
                        },
                    ),
                ),
            ),
        ),
        Identifier("if") to lazyOf(
            object : AbstractionValue() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTupleValue

                    val conditionValue = args.get(key = Identifier(name = "condition")) as BooleanValue

                    return if (conditionValue.isTrue()) {
                        args.get(key = Identifier(name = "then"))
                    } else {
                        args.get(key = Identifier(name = "else"))
                    }
                }
            },
        ),
        Identifier("isA") to lazyOf(
            object : AbstractionValue() {
                override fun compute(argument: Value): Value {
                    val args = argument as UnorderedTupleValue

                    val instanceValue = args.get(key = Identifier(name = "instance"))
                    val classValue = args.get(key = Identifier(name = "class"))

                    val instancePrototypeValue = (instanceValue as CallableValue).call(
                        argument = ClassModule.instancePrototypeIdentifier,
                    )

                    val classPrototypeValue = (classValue as CallableValue).call(
                        argument = ClassModule.classPrototypeIdentifier,
                    )

                    val instancePrototypeTag = (instancePrototypeValue as CallableValue).call(
                        argument = ClassModule.classTagIdentifier,
                    )

                    val classPrototypeTag = (classPrototypeValue as CallableValue).call(
                        argument = ClassModule.classTagIdentifier,
                    )

                    return BooleanValue(
                        value = instancePrototypeTag == classPrototypeTag,
                    )
                }
            },
        ),
        Identifier("panic") to lazyOf(
            object : AbstractionValue() {
                override fun compute(argument: Value): Value {
                    throw RuntimeException("Panic!")
                }
            },
        ),
    )
}

package com.github.cubuspl42.sigmaLang.core.values.builtin

import com.github.cubuspl42.sigmaLang.core.values.AbstractionValue
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.IndexableValue
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value

object ClassModule : IndexableValue() {
    val classPrototypeIdentifier = Identifier(name = "__class_prototype__")
    val classTagIdentifier = Identifier(name = "__class_tag__")
    val instancePrototypeIdentifier = Identifier(name = "__instance_prototype__")
    val thisIdentifier = Identifier(name = "this")

    private fun buildProxyMethod(
        methodName: Identifier,
    ): AbstractionValue = object : AbstractionValue() {
        override fun compute(argument: Value): Value {
            val self = (argument as UnorderedTupleValue).get(key = thisIdentifier) as IndexableValue

            val instancePrototype = self.get(key = instancePrototypeIdentifier) as IndexableValue

            val originalMethod = instancePrototype.get(key = methodName) as AbstractionValue

            return originalMethod.call(argument = argument)
        }
    }

    object Of : AbstractionValue() {
        override fun compute(
            argument: Value,
        ): Value {
            argument as IndexableValue

            val tag = argument.get(key = Identifier.of("tag")) as Identifier
            val instanceConstructorName = argument.get(key = Identifier.of("instanceConstructorName")) as Identifier
            val methods = argument.get(key = Identifier.of("methods")) as UnorderedTupleValue

            val prototype = methods.extendWith(
                key = classTagIdentifier,
                value = tag,
            )

            val instanceConstructor = object : AbstractionValue() {
                override fun compute(argument: Value): Value = (argument as UnorderedTupleValue).extendWith(
                    key = instancePrototypeIdentifier,
                    value = prototype,
                )
            }

            val proxyMethods: Map<Identifier, Lazy<AbstractionValue>> = methods.valueByKey.mapValues { (name, _) ->
                lazyOf(buildProxyMethod(methodName = name))
            }

            return UnorderedTupleValue(
                valueByKey = mapOf(
                    classPrototypeIdentifier to lazyOf(prototype),
                    instanceConstructorName to lazyOf(instanceConstructor),
                ) + proxyMethods,
            )
        }

        fun call(
            tag: Identifier,
            instanceConstructorName: Identifier,
            methodByName: Map<Identifier, AbstractionValue>,
        ): Value = compute(
            argument = UnorderedTupleValue(
                valueByKey = mapOf(
                    Identifier.of("tag") to lazyOf(tag),
                    Identifier.of("instanceConstructorName") to lazyOf(instanceConstructorName),
                    Identifier.of("methods") to lazyOf(
                        UnorderedTupleValue(
                            valueByKey = methodByName.mapValues { (_, method) -> lazyOf(method) },
                        ),
                    ),
                ),
            ),
        )
    }

    override val valueByKey = mapOf(
        Identifier("of") to lazyOf(Of),
    )
}

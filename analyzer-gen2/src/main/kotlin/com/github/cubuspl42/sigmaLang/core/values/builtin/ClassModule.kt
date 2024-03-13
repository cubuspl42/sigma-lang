package com.github.cubuspl42.sigmaLang.core.values.builtin

import com.github.cubuspl42.sigmaLang.core.ClassExpression.Companion.classPrototypeIdentifier
import com.github.cubuspl42.sigmaLang.core.ClassExpression.Companion.instancePrototypeIdentifier
import com.github.cubuspl42.sigmaLang.core.ClassExpression.Companion.thisIdentifier
import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Indexable
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

object ClassModule : Indexable() {
    private fun buildProxyMethod(
        methodName: Identifier,
    ): Abstraction = object : Abstraction() {
        override fun compute(argument: Value): Value {
            val self = (argument as UnorderedTuple).get(key = thisIdentifier) as Indexable

            val instancePrototype = self.get(key = instancePrototypeIdentifier) as Indexable

            val originalMethod = instancePrototype.get(key = methodName) as Abstraction

            return originalMethod.call(argument = argument)
        }
    }

    object Of : Abstraction() {
        override fun compute(
            argument: Value,
        ): Value {
            argument as Indexable

            val tag = argument.get(key = classPrototypeIdentifier) as Identifier
            val instanceConstructorName = argument.get(key = instancePrototypeIdentifier) as Identifier
            val methods = argument.valueByKey.mapValues { it.value as Abstraction }

            val prototype: Value = UnorderedTuple(
                valueByKey = methods.mapValues { (_, method) ->
                    lazyOf(method)
                },
            ).extendWith(
                key = classPrototypeIdentifier,
                value = tag,
            )

            val instanceConstructor = object : Abstraction() {
                override fun compute(argument: Value): Value = (argument as UnorderedTuple).extendWith(
                    key = instancePrototypeIdentifier,
                    value = prototype,
                )
            }

            val proxyMethods: Map<Identifier, Lazy<Abstraction>> = methods.mapValues { (name, _) ->
                lazyOf(buildProxyMethod(methodName = name))
            }

            return UnorderedTuple(
                valueByKey = mapOf(
                    classPrototypeIdentifier to lazyOf(prototype),
                    instanceConstructorName to lazyOf(instanceConstructor),
                ) + proxyMethods,
            )
        }

        fun call(
            tag: Identifier,
            instanceConstructorName: Identifier,
            methodByName: Map<Identifier, Abstraction>,
        ): Value = compute(
            argument = UnorderedTuple(
                valueByKey = mapOf(
                    Identifier.of("tag") to lazyOf(tag),
                    Identifier.of("instanceConstructorName") to lazyOf(instanceConstructorName),
                    Identifier.of("methods") to lazyOf(
                        UnorderedTuple(
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

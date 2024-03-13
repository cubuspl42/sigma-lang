package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

abstract class ClassExpression : ShadowExpression() {
    companion object {
        val classPrototypeIdentifier = Identifier(name = "__class_prototype__")
        val classTagIdentifier = Identifier(name = "__class_tag__")
        val instancePrototypeIdentifier = Identifier(name = "__instance_prototype__")
        val thisIdentifier = Identifier(name = "this")
        val argsIdentifier = Identifier(name = "__args__")
    }

    final override val rawExpression: Expression
        get() = rawClassExpression

    val prototype: Call
        get() = rawClassExpression.readField(fieldName = classPrototypeIdentifier)

    abstract val rawClassExpression: Expression
}

class ClassConstructor(
    private val rawClassConstructor: Expression,
) : ClassExpression() {
    abstract class Config {
        abstract val tag: Identifier

        abstract val constructorName: Identifier

        abstract fun createMethodDefinitions(
            classReference: ClassReference,
        ): Set<MethodDefinition.Config>
    }

    data class MethodDefinition(
        val name: Identifier,
        val implementation: AbstractionConstructor,
    ) {
        abstract class Config {
            fun createOriginal() = MethodDefinition(
                name = name,
                implementation = AbstractionConstructor.looped1 { methodArgumentReference ->
                    val thisReference = methodArgumentReference.readField(
                        fieldName = thisIdentifier,
                    )

                    createImplementation(
                        thisReference = thisReference,
                    ).call(
                        passedArgument = methodArgumentReference.readField(
                            fieldName = argsIdentifier,
                        )
                    )
                },
            )

            fun buildProxy() = MethodDefinition(
                name = name,
                implementation = AbstractionConstructor.looped1 { argumentReference ->
                    val thisExpression = argumentReference.readField(
                        fieldName = thisIdentifier,
                    )

                    val instancePrototype = thisExpression.readField(
                        fieldName = instancePrototypeIdentifier,
                    )

                    val method = instancePrototype.readField(
                        fieldName = name,
                    )

                    method.call(
                        passedArgument = UnorderedTupleConstructor(
                            valueByKey = mapOf(
                                thisIdentifier to lazyOf(thisExpression),
                                argsIdentifier to lazyOf(argumentReference),
                            ),
                        ),
                    )
                },
            )

            abstract val name: Identifier

            abstract fun createImplementation(
                thisReference: Expression,
            ): AbstractionConstructor
        }

        fun toEntry() = UnorderedTupleConstructor.Entry(
            key = name,
            value = lazyOf(implementation),
        )
    }

    companion object {
        fun create(
            builtinModule: BuiltinModuleReference,
            config: Config,
        ): ClassConstructor {
            fun buildInstanceConstructor(
                classReference: ClassReference,
            ): AbstractionConstructor {
                val unionWith = builtinModule.dictClass.unionWith

                return AbstractionConstructor.looped1 { argumentReference ->
                    unionWith.call(
                        dict = argumentReference,
                        otherDict = UnorderedTupleConstructor(
                            valueByKey = mapOf(
                                instancePrototypeIdentifier to lazyOf(classReference.prototype),
                            ),
                        ),
                    ).rawExpression
                }
            }

            fun buildPrototypeConstructor(
                methodDefinitionConfigs: Set<MethodDefinition.Config>,
            ): UnorderedTupleConstructor {
                val originalMethodDefinitions = methodDefinitionConfigs.mapUniquely { methodDefinitionStub ->
                    methodDefinitionStub.createOriginal()
                }

                val prototypeConstructor = UnorderedTupleConstructor.fromEntries(
                    entries = originalMethodDefinitions.mapUniquely {
                        it.toEntry()
                    } + UnorderedTupleConstructor.Entry(
                        key = classTagIdentifier,
                        value = lazyOf(config.tag.toLiteral()),
                    ),
                )

                return prototypeConstructor
            }

            val (rootKnotConstructor, _) = KnotConstructor.looped { rawClassReference ->
                val selfReference = ClassReference(
                    rawClassReference = rawClassReference,
                )

                val instanceConstructor = buildInstanceConstructor(
                    classReference = selfReference,
                )
                val methodDefinitionBuilders = config.createMethodDefinitions(classReference = selfReference)

                val prototypeConstructor = buildPrototypeConstructor(
                    methodDefinitionConfigs = methodDefinitionBuilders,
                )

                val proxyMethodDefinitions = methodDefinitionBuilders.mapUniquely {
                    it.buildProxy()
                }

                val rootTupleConstructor = UnorderedTupleConstructor.fromEntries(
                    entries = setOfNotNull(
                        UnorderedTupleConstructor.Entry(
                            key = config.constructorName,
                            value = lazyOf(instanceConstructor),
                        ),
                        UnorderedTupleConstructor.Entry(
                            key = classPrototypeIdentifier,
                            value = lazyOf(prototypeConstructor),
                        ),
                    ) + proxyMethodDefinitions.mapUniquely {
                        it.toEntry()
                    },
                )

                Pair(
                    rootTupleConstructor,
                    Unit,
                )
            }

            return ClassConstructor(
                rawClassConstructor = rootKnotConstructor,
            )
        }
    }

    override val rawClassExpression: Expression
        get() = rawClassConstructor
}

class ClassReference(
    private val rawClassReference: Reference,
) : ClassExpression() {
    override val rawClassExpression: Expression = rawClassReference.rawExpression
}

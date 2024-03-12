package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

abstract class ClassBuilder(
    private val tag: Identifier,
    private val constructorName: Identifier,
) : ExpressionBuilder<ClassBuilder.Constructor>() {
    data class Constructor(
        val rawConstructor: Expression,
        val prototypeConstructor: UnorderedTupleConstructor,
        val proxyMethodDefinitions: Set<MethodDefinition>,
    ) : ShadowExpression() {
        data class MethodDefinition(
            val name: Identifier,
            val outerImplementationConstructor: AbstractionConstructor,
        ) {
            fun toEntry() = UnorderedTupleConstructor.Entry(
                key = name,
                value = lazyOf(outerImplementationConstructor),
            )
        }

        override val rawExpression: Expression = rawConstructor
    }

    data class PartialConstructor(
        val prototypeConstructor: UnorderedTupleConstructor,
        val proxyMethodDefinitions: Set<Constructor.MethodDefinition>,
    )

    data class Reference(
        val prototypeReference: Expression,
    ) {
        companion object {
            fun wrap(rawClassReference: KnotReference): Reference = Reference(
                prototypeReference = rawClassReference.readField(
                    fieldName = classPrototypeIdentifier,
                ),
            )
        }

        fun referOriginalMethod(
            methodName: Identifier,
        ): Expression = prototypeReference.readField(
            fieldName = methodName,
        )
    }

    abstract class MethodDefinitionBuilder(
        val name: Identifier,
    ) {
        fun buildOriginal(
            buildContext: Expression.BuildContext,
        ) = Constructor.MethodDefinition(
            name = name,
            outerImplementationConstructor = AbstractionConstructor.looped1 { methodArgumentReference ->
                val thisReference = methodArgumentReference.readField(
                    fieldName = thisIdentifier,
                )

                buildImplementation(
                    thisReference = thisReference,
                ).build(
                    buildContext = buildContext,
                ).call(
                    passedArgument = methodArgumentReference.readField(
                        fieldName = argsIdentifier,
                    )
                )
            },
        )

        fun buildProxy() = Constructor.MethodDefinition(
            name = name,
            outerImplementationConstructor = AbstractionConstructor.looped1 { argumentReference ->
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

        abstract fun buildImplementation(
            thisReference: Expression,
        ): ExpressionBuilder<AbstractionConstructor>
    }

    companion object {
        val classPrototypeIdentifier = Identifier(name = "__class_prototype__")
        val classTagIdentifier = Identifier(name = "__class_tag__")
        val instancePrototypeIdentifier = Identifier(name = "__instance_prototype__")
        val thisIdentifier = Identifier(name = "this")
        val argsIdentifier = Identifier(name = "__args__")
    }

    abstract fun buildMethods(
        classReference: Reference,
    ): Set<MethodDefinitionBuilder>

    override fun build(
        buildContext: Expression.BuildContext,
    ): Constructor {
        val (rootKnotConstructor, partialConstructor) = KnotConstructor.looped { rawClassReference ->
            val classReference = Reference.wrap(rawClassReference)

            val instanceConstructor = buildInstanceConstructor(
                buildContext = buildContext,
                classReference = classReference,
            )
            val methodDefinitionBuilders = buildMethods(classReference = classReference)

            val prototypeConstructor = buildPrototypeConstructor(
                methodDefinitionBuilders = methodDefinitionBuilders,
                buildContext = buildContext,
            )

            val proxyMethodDefinitions = methodDefinitionBuilders.mapUniquely {
                it.buildProxy()
            }

            val rootTupleConstructor = UnorderedTupleConstructor.fromEntries(
                entries = setOfNotNull(
                    UnorderedTupleConstructor.Entry(
                        key = constructorName,
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
                PartialConstructor(
                    prototypeConstructor = prototypeConstructor,
                    proxyMethodDefinitions = proxyMethodDefinitions,
                ),
            )
        }

        return Constructor(
            rawConstructor = rootKnotConstructor,
            prototypeConstructor = partialConstructor.prototypeConstructor,
            proxyMethodDefinitions = partialConstructor.proxyMethodDefinitions,
        )
    }

    private fun buildInstanceConstructor(
        buildContext: Expression.BuildContext,
        classReference: Reference,
    ): AbstractionConstructor {
        val unionWith = buildContext.builtinModule.dictClass.unionWith

        return AbstractionConstructor.looped1 { argumentReference ->
            unionWith.call(
                dict = argumentReference,
                otherDict = UnorderedTupleConstructor(
                    valueByKey = mapOf(
                        instancePrototypeIdentifier to lazyOf(classReference.prototypeReference),
                    ),
                ),
            ).rawExpression
        }
    }

    private fun buildPrototypeConstructor(
        methodDefinitionBuilders: Set<ClassBuilder.MethodDefinitionBuilder>,
        buildContext: Expression.BuildContext,
    ): UnorderedTupleConstructor {
        val originalMethodDefinitions = methodDefinitionBuilders.mapUniquely { methodDefinitionStub ->
            methodDefinitionStub.buildOriginal(buildContext = buildContext)
        }

        val prototypeConstructor = UnorderedTupleConstructor.fromEntries(
            entries = originalMethodDefinitions.mapUniquely {
                it.toEntry()
            } + UnorderedTupleConstructor.Entry(
                key = classTagIdentifier,
                value = lazyOf(tag.toLiteral()),
            ),
        )

        return prototypeConstructor
    }
}

fun ShadowExpression.isA(
    class_: ShadowExpression,
): ExpressionBuilder<ShadowExpression> = object : ExpressionBuilder<ShadowExpression>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): ShadowExpression = buildContext.builtinModule.isAFunction.call(
        instance = this@isA,
        class_ = class_,
    )
}

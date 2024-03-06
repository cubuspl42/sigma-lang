package com.github.cubuspl42.sigmaLang.core.concepts

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

abstract class ShadowExpression {
    abstract val rawExpression: Expression
}

abstract class ExpressionBuilder<out TExpression : ShadowExpression> {
    companion object {
        fun <TExpression : ShadowExpression> pure(
            expression: TExpression,
        ): ExpressionBuilder<TExpression> = object : ExpressionBuilder<TExpression>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression = expression
        }
    }

    abstract fun build(
        buildContext: Expression.BuildContext,
    ): TExpression

    fun buildRaw(
        buildContext: Expression.BuildContext,
    ): Expression = build(
        buildContext = buildContext,
    ).rawExpression
}

abstract class IntermediateExpressionBuilder<out TExpression : ShadowExpression> : ExpressionBuilder<TExpression>() {
    final override fun build(buildContext: Expression.BuildContext): TExpression {
        val intermediateBuilder = buildIntermediate(buildContext = buildContext)

        return intermediateBuilder.build(buildContext = buildContext)
    }

    abstract fun buildIntermediate(
        buildContext: Expression.BuildContext,
    ): ExpressionBuilder<TExpression>
}

//class TerminalExpressionBuilder<out TExpression : Expression> : ExpressionBuilder<TExpression>() {
//    private val expression: TExpression
//
//
//    override fun build(
//        buildContext: Expression.BuildContext,
//    ): TExpression = expression
//
//    abstract fun buildTerminal(
//        buildContext: Expression.BuildContext,
//    ): Expression
//}


abstract class ClassBuilder(
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
        fun buildOriginal() = Constructor.MethodDefinition(
            name = name,
            outerImplementationConstructor = AbstractionConstructor.looped { methodArgumentReference ->
                val thisReference = methodArgumentReference.readField(
                    fieldName = thisIdentifier,
                )

                buildImplementation(
                    thisReference = thisReference,
                ).call(
                    passedArgument = methodArgumentReference.readField(
                        fieldName = argsIdentifier,
                    )
                )
            },
        )

        fun buildProxy() = Constructor.MethodDefinition(
            name = name,
            outerImplementationConstructor = AbstractionConstructor.looped { argumentReference ->
                val self = argumentReference.readField(
                    fieldName = thisIdentifier,
                )

                val instancePrototype = self.readField(
                    fieldName = instancePrototypeIdentifier,
                )

                val method = instancePrototype.readField(
                    fieldName = name,
                )

                method.call(
                    passedArgument = argumentReference,
                )
            },
        )

        abstract fun buildImplementation(
            thisReference: Expression,
        ): AbstractionConstructor
    }

    companion object {
        val classPrototypeIdentifier = Identifier(name = "__class_prototype__")
        val instancePrototypeIdentifier = Identifier(name = "__instance_prototype__")
        val thisIdentifier = Identifier(name = "__this__")
        val argsIdentifier = Identifier(name = "__args__")
    }

    abstract fun buildMethods(
        classReference: Reference,
    ): Set<MethodDefinitionBuilder>

    override fun build(
        buildContext: Expression.BuildContext,
    ): Constructor {
        val unionWith = buildContext.referBuiltin(
            name = Identifier(name = "unionWith"),
        )

        val (_, constructor) = KnotConstructor.looped { rawClassReference ->
            val classReference = Reference.wrap(rawClassReference)

            val methodDefinitionBuilders = buildMethods(classReference = classReference)

            val proxyMethodDefinitions = methodDefinitionBuilders.mapUniquely {
                it.buildProxy()
            }

            val instanceConstructorEntry = UnorderedTupleConstructor.Entry(
                key = constructorName,
                value = lazyOf(
                    AbstractionConstructor.looped { argumentReference ->
                        unionWith.call(
                            passedArgument = UnorderedTupleConstructor.of(
                                Identifier(name = "first") to lazyOf(argumentReference),
                                Identifier(name = "second") to lazyOf(
                                    UnorderedTupleConstructor(
                                        valueByKey = mapOf(
                                            instancePrototypeIdentifier to lazyOf(classReference.prototypeReference),
                                        ),
                                    ),
                                ),
                            ),
                        )
                    },
                ),
            )

            val originalMethodDefinitions = methodDefinitionBuilders.mapUniquely { methodDefinitionStub ->
                methodDefinitionStub.buildOriginal()
            }

            val prototypeConstructor = UnorderedTupleConstructor.fromEntries(
                entries = originalMethodDefinitions.mapUniquely { it.toEntry() },
            )

            val prototypeConstructorEntry = UnorderedTupleConstructor.Entry(
                key = classPrototypeIdentifier, value = lazyOf(
                    prototypeConstructor,
                )
            )

            val rawConstructor = UnorderedTupleConstructor.fromEntries(
                entries = setOfNotNull(
                    instanceConstructorEntry,
                    prototypeConstructorEntry,
                ) + proxyMethodDefinitions.mapUniquely {
                    it.toEntry()
                },
            )

            Pair(
                rawConstructor,
                Constructor(
                    rawConstructor = rawConstructor,
                    prototypeConstructor = prototypeConstructor,
                    proxyMethodDefinitions = proxyMethodDefinitions,
                ),
            )
        }

        return constructor
    }
}

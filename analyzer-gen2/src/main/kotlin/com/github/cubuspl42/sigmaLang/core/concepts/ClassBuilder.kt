package com.github.cubuspl42.sigmaLang.core.concepts

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
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

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            function: (TExpression1, TExpression2) -> TExpression3,
        ): ExpressionBuilder<TExpression3> = object : ExpressionBuilder<TExpression3>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression3 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression> map2Joined(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            function: (TExpression1, TExpression2) -> ExpressionBuilder<TExpression3>,
        ): ExpressionBuilder<TExpression3> = object : ExpressionBuilder<TExpression3>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression3 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
            ).build(
                buildContext = buildContext,
            )
        }

        fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression, TExpression3 : ShadowExpression, TExpression4 : ShadowExpression> map3Joined(
            builder1: ExpressionBuilder<TExpression1>,
            builder2: ExpressionBuilder<TExpression2>,
            builder3: ExpressionBuilder<TExpression3>,
            function: (TExpression1, TExpression2, TExpression3) -> ExpressionBuilder<TExpression4>,
        ): ExpressionBuilder<TExpression4> = object : ExpressionBuilder<TExpression4>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): TExpression4 = function(
                builder1.build(buildContext = buildContext),
                builder2.build(buildContext = buildContext),
                builder3.build(buildContext = buildContext),
            ).build(
                buildContext = buildContext,
            )
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

    fun asStub(): ExpressionStub<TExpression> = ExpressionStub.pure(this)
}

fun <TExpression : ShadowExpression, RExpression : ShadowExpression> ExpressionBuilder<TExpression>.map(
    function: (TExpression) -> RExpression,
): ExpressionBuilder<RExpression> = object : ExpressionBuilder<RExpression>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): RExpression = function(
        this@map.build(buildContext = buildContext),
    )
}

fun <TExpression1 : ShadowExpression, TExpression2 : ShadowExpression> ExpressionBuilder<TExpression1>.joinOf(
    extract: (TExpression1) -> TExpression2,
): ExpressionBuilder<TExpression2> = object : ExpressionBuilder<TExpression2>() {
    override fun build(
        buildContext: Expression.BuildContext,
    ): TExpression2 = this@joinOf.map(extract).build(
        buildContext = buildContext,
    )
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
        val unionWith = buildContext.referBuiltin(
            name = Identifier(name = "unionWith"),
        )

        val (rootKnotConstructor, partialConstructor) = KnotConstructor.looped { rawClassReference ->
            val classReference = Reference.wrap(rawClassReference)

            val methodDefinitionBuilders = buildMethods(classReference = classReference)

            val proxyMethodDefinitions = methodDefinitionBuilders.mapUniquely {
                it.buildProxy()
            }

            val instanceConstructorEntry = UnorderedTupleConstructor.Entry(
                key = constructorName,
                value = lazyOf(
                    AbstractionConstructor.looped1 { argumentReference ->
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
                methodDefinitionStub.buildOriginal(buildContext = buildContext)
            }

            val prototypeConstructor = UnorderedTupleConstructor.fromEntries(
                entries = originalMethodDefinitions.mapUniquely { it.toEntry() },
            )

            val prototypeConstructorEntry = UnorderedTupleConstructor.Entry(
                key = classPrototypeIdentifier,
                value = lazyOf(prototypeConstructor),
            )

            val rootTupleConstructor = UnorderedTupleConstructor.fromEntries(
                entries = setOfNotNull(
                    instanceConstructorEntry,
                    prototypeConstructorEntry,
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
}

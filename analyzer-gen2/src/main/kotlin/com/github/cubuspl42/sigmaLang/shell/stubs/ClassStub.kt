package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

object ClassStub {
    val classPrototypeIdentifier = Identifier(name = "__class_prototype__")
    val instancePrototypeIdentifier = Identifier(name = "__instance_prototype__")

    data class MethodDefinitionStub(
        val name: Identifier,
        val methodConstructorStub: AbstractionConstructorStub,
    ) {
        fun buildOriginal() = UnorderedTupleConstructorStub.Entry(
            key = name,
            valueStub = methodConstructorStub.toMethod(),
        )

        fun buildProxy() = UnorderedTupleConstructorStub.Entry(
            key = name,
            valueStub = AbstractionConstructor.looped { argumentReference ->
                val self = argumentReference.readField(
                    fieldName = Identifier(name = "this"),
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
            }.asStub(),
        )
    }

    fun of(
        constructorName: Identifier,
        methodDefinitionStubs: Set<MethodDefinitionStub>,
    ): ExpressionStub<*> {
        return object : ExpressionStub<Expression>() {
            override fun form(context: FormationContext): Lazy<Expression> {
                val unionWith = referBuiltin(
                    name = Identifier(name = "unionWith"),
                ).formStrict(
                    context = context,
                )

                val proxyMethodDefinitions = methodDefinitionStubs.mapUniquely { methodDefinitionStub ->
                    methodDefinitionStub.buildProxy()
                }

                return LocalScopeStub.of { classReference ->
                    val classPrototypeReference = classReference.readField(
                        fieldName = classPrototypeIdentifier,
                    )

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
                                                    instancePrototypeIdentifier to lazyOf(classPrototypeReference),
                                                ),
                                            ),
                                        ),
                                    ),
                                )
                            },
                        ),
                    )

                    val classPrototypeConstructorStub = UnorderedTupleConstructorStub.Entry(
                        key = classPrototypeIdentifier,
                        valueStub = UnorderedTupleConstructorStub.fromEntries(
                            entries = methodDefinitionStubs.mapUniquely { methodDefinitionStub ->
                                methodDefinitionStub.buildOriginal()
                            },
                        ),
                    )

                    UnorderedTupleConstructorStub.fromEntries(
                        entries = setOfNotNull(
                            instanceConstructorEntry.asStub(),
                            classPrototypeConstructorStub,
                        ) + proxyMethodDefinitions,
                    )
                }.form(context = context)
            }
        }
    }
}

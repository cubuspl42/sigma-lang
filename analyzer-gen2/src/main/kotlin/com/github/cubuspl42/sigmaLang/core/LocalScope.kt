package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier

object LocalScope {
    class Reference(
        private val knotReference: KnotReference,
    ) {
        fun referDefinitionInitializer(
            name: Identifier,
        ): ShadowExpression = knotReference.readField(fieldName = name)
    }

    class Constructor(
        private val knotConstructor: KnotConstructor,
        private val definitions: Set<Definition>,
    ) : ShadowExpression() {
        data class Definition(
            val name: Identifier,
            val initializer: ShadowExpression,
        )

        companion object {
            fun make(
                makeDefinitions: (Reference) -> Set<Definition>,
            ): Constructor {
                val (knotConstructor, definitions) = KnotConstructor.looped { knotReference ->
                    val reference = Reference(knotReference = knotReference)

                    val definitions = makeDefinitions(reference)

                    Pair(
                        UnorderedTupleConstructor(
                            valueByKey = definitions.associate {
                                it.name to lazyOf(it.initializer)
                            },
                        ),
                        definitions,
                    )
                }

                return Constructor(
                    knotConstructor = knotConstructor,
                    definitions = definitions,
                )
            }

            fun bindSingle(
                expression: ShadowExpression,
                makeResult: (ShadowExpression) -> ShadowExpression,
            ): ShadowExpression {
                val boundIdentifier = Identifier.of("bound")
                val resultIdentifier = Identifier.of("result")

                val constructor = make { reference ->
                    val boundReference = reference.referDefinitionInitializer(
                        name = boundIdentifier,
                    )

                    val result = makeResult(boundReference)

                    setOf(
                        Definition(
                            name = boundIdentifier,
                            initializer = expression,
                        ),
                        Definition(
                            name = resultIdentifier,
                            initializer = result,
                        ),
                    )
                }

                val result = constructor.getDefinitionInitializer(
                    name = resultIdentifier,
                )

                return result
            }
        }

        class Builder(
            private val buildDefinitions: (Reference) -> Set<Definition>,
        ) : ExpressionBuilder<Constructor>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Constructor = make(
                makeDefinitions = buildDefinitions,
            )
        }

        fun getDefinitionInitializer(name: Identifier): ShadowExpression =
            knotConstructor.readField(fieldName = name)

        override val rawExpression: Expression
            get() = knotConstructor
    }
}

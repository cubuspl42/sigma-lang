package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

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
        sealed class Definition

        data class SimpleDefinition(
            val name: Identifier,
            val initializer: ShadowExpression,
        ) : Definition() {
            fun toEntry(): UnorderedTupleConstructor.Entry = UnorderedTupleConstructor.Entry(
                key = name,
                value = lazy { initializer.rawExpression },
            )
        }

        sealed class ComplexDefinition : Definition() {
            abstract fun toConstructor(): ExpressionBuilder<ShadowExpression>
        }

        data class ListUnconsDefinition(
            val headName: Identifier,
            val tailName: Identifier,
            val listInitializer: ShadowExpression,
        ) : ComplexDefinition() {
            override fun toConstructor() = object : ExpressionBuilder<ShadowExpression>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ): ShadowExpression {
                    val listClass = buildContext.builtinModule.listClass

                    return listInitializer.bindToReference { listInitializerReference ->
                        UnorderedTupleConstructor.fromEntries(
                            UnorderedTupleConstructor.Entry(
                                key = headName,
                                value = lazy {
                                    listClass.head.call(list = listInitializerReference)
                                },
                            ),
                            UnorderedTupleConstructor.Entry(
                                key = tailName,
                                value = lazy {
                                    listClass.tail.call(list = listInitializerReference)
                                },
                            ),
                        )
                    }
                }
            }
        }

        companion object {
            fun make(
                makeDefinitions: (Reference) -> Set<Definition>,
            ): ExpressionBuilder<Constructor> = object : ExpressionBuilder<Constructor>() {
                override fun build(buildContext: Expression.BuildContext): Constructor {
                    val unionWith = buildContext.builtinModule.dictClass.unionWith

                    val (knotConstructor, definitions) = KnotConstructor.looped { knotReference ->
                        val reference = Reference(knotReference = knotReference)

                        val definitions = makeDefinitions(reference)

                        val simpleDefinitions = definitions.filterIsInstance<SimpleDefinition>()
                        val complexDefinitions = definitions.filterIsInstance<ComplexDefinition>()

                        val fullTupleExpression = complexDefinitions.fold(
                            initial = UnorderedTupleConstructor.fromEntries(
                                simpleDefinitions.mapUniquely { it.toEntry() },
                            ),
                        ) { accTupleExpression: ShadowExpression, complexDefinition ->
                            val subTupleExpression = complexDefinition.toConstructor().build(
                                buildContext = buildContext,
                            )

                            unionWith.call(
                                dict = accTupleExpression,
                                otherDict = subTupleExpression,
                            )
                        }

                        Pair(
                            fullTupleExpression.rawExpression,
                            definitions,
                        )
                    }

                    return Constructor(
                        knotConstructor = knotConstructor,
                        definitions = definitions,
                    )
                }
            }
        }

        fun getDefinitionInitializer(name: Identifier): ShadowExpression = knotConstructor.readField(fieldName = name)

        override val rawExpression: Expression
            get() = knotConstructor
    }
}

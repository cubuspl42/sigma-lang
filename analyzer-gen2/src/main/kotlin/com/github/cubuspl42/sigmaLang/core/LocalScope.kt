package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.bindToReference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

fun Expression.asDefinitionBlock(): LocalScope.DefinitionBlock = object : LocalScope.DefinitionBlock() {
    override val blockExpression: Expression
        get() = this@asDefinitionBlock
}

object LocalScope {
    class Reference(
        private val rawReference: Expression,
    ) {
        fun referDefinitionInitializer(
            name: Identifier,
        ): Expression = rawReference.readField(fieldName = name)
    }

    abstract class DefinitionBlock : ShadowExpression() {
        companion object {
            val Empty = object : DefinitionBlock() {
                override val blockExpression: Expression = UnorderedTupleConstructor.Empty
            }

            fun makeSimple(
                definitions: Set<Constructor.SimpleDefinition>,
            ): DefinitionBlock {
                val tupleConstructor = UnorderedTupleConstructor.fromEntries(
                    entries = definitions.mapUniquely { it.toEntry() },
                )

                return object : DefinitionBlock() {
                    override val blockExpression: Expression = tupleConstructor
                }
            }
        }

        final override val rawExpression: Expression
            get() = blockExpression

        fun getInitializer(
            name: Identifier,
        ): Expression = blockExpression.readField(fieldName = name)

        fun mergeWith(
            dictClass: BuiltinModuleReference.DictClassReference,
            other: DefinitionBlock,
        ): DefinitionBlock = object : DefinitionBlock() {
            override val blockExpression = dictClass.unionWith.call(
                dict = this@DefinitionBlock.blockExpression,
                otherDict = other.blockExpression,
            )
        }

        /**
         * An expression evaluating to an unordered tuple, where keys are definition names and the respective values
         * are definitions' initializers.
         */
        abstract val blockExpression: Expression
    }

    class Constructor(
        val knotConstructor: KnotConstructor,
        val definitions: Set<Definition>,
    ) : ShadowExpression() {
        sealed class Definition {
            abstract val initializer: Expression
        }

        data class SimpleDefinition(
            val name: Identifier,
            override val initializer: Expression,
        ) : Definition() {
            fun toEntry(): UnorderedTupleConstructor.Entry = UnorderedTupleConstructor.Entry(
                key = name,
                value = lazyOf(initializer),
            )
        }

        data class PatternDefinition(
            val builtinModuleReference: BuiltinModuleReference,
            val pattern: Pattern,
            override val initializer: Expression,
        ) : Definition() {
            val guardedDefinitionBlock: DefinitionBlock
                get() {
                    val patternApplication = pattern.apply(expression = initializer)

                    return builtinModuleReference.ifFunction.call(
                        condition = patternApplication.condition,
                        thenCase = patternApplication.definitionBlock.rawExpression,
                        elseCase = builtinModuleReference.panicFunction.call(),
                    ).asDefinitionBlock()
                }
        }

        companion object {
            fun make(
                makeDefinitions: (Reference) -> Set<Definition>,
            ): ExpressionBuilder<Constructor> = object : ExpressionBuilder<Constructor>() {
                override fun build(buildContext: Expression.BuildContext): Constructor {
                    val (knotConstructor, definitions) = KnotConstructor.looped { knotReference ->
                        val reference = Reference(rawReference = knotReference)

                        val definitions = makeDefinitions(reference)

                        val simpleDefinitions = definitions.filterIsInstance<SimpleDefinition>().toSet()
                        val patternDefinitions = definitions.filterIsInstance<PatternDefinition>()

                        val fullDefinitionBlock = patternDefinitions.fold(
                            initial = DefinitionBlock.makeSimple(
                                definitions = simpleDefinitions,
                            )
                        ) { accDefinitionBlock: DefinitionBlock, patternDefinition ->
                            accDefinitionBlock.mergeWith(
                                dictClass = buildContext.builtinModule.dictClass,
                                patternDefinition.guardedDefinitionBlock,
                            )
                        }

                        Pair(
                            fullDefinitionBlock.rawExpression,
                            definitions,
                        )
                    }

                    return Constructor(
                        knotConstructor = knotConstructor,
                        definitions = definitions,
                    )
                }
            }

            fun makeWithResult(
                makeDefinitions: (Reference) -> Set<Definition>,
                makeResult: (Reference) -> Expression,
            ): ExpressionBuilder<Expression> = make(
                makeDefinitions = makeDefinitions,
            ).map { localScopeConstructor ->
                localScopeConstructor.rawExpression.bindToReference { localScopeReference ->
                    val reference = Reference(rawReference = localScopeReference)

                    makeResult(reference)
                }
            }
        }

        override val rawExpression: Expression
            get() = knotConstructor
    }
}

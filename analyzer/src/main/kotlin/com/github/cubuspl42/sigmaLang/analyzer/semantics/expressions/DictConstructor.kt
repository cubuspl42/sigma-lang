package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.PrimitiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.utils.SetUtils

abstract class DictConstructor : FirstOrderExpression() {
    abstract override val term: DictConstructorTerm

    abstract val associations: List<Association>

    abstract class Association {
        abstract val key: Expression

        abstract val value: Expression

        data class Analysis(
            val keyAnalysis: Expression.TypeInference,
            val valueAnalysis: Expression.TypeInference,
        ) {
            val inferredKeyType: SpecificType
                get() = keyAnalysis.inferredType as SpecificType

            val inferredValueType: SpecificType
                get() = valueAnalysis.inferredType as SpecificType
        }

        companion object {
            fun build(
                context: BuildContext,
                term: DictConstructorTerm.Association,
            ): Stub<Association> = object : Stub<Association> {
                override val resolved: Association by lazy {
                    object : Association() {
                        override val key: Expression by lazy {
                            Expression.build(
                                context = context,
                                term = term.key,
                            ).resolved
                        }

                        override val value: Expression by lazy {
                            Expression.build(
                                context = context,
                                term = term.value,
                            ).resolved
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun build(
            context: BuildContext,
            term: DictConstructorTerm,
        ): Stub<DictConstructor> = object : Stub<DictConstructor> {
            override val resolved: DictConstructor by lazy {
                object : DictConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: DictConstructorTerm = term

                    override val associations: List<Association> by lazy {
                        term.associations.map {
                            Association.build(
                                context = context,
                                term = it,
                            ).resolved
                        }
                    }
                }
            }
        }
    }

    sealed interface InferredKeyTypeError : SemanticError

    data class InconsistentKeyTypeError(
        override val location: SourceLocation,
    ) : InferredKeyTypeError

    data class NonPrimitiveKeyTypeError(
        override val location: SourceLocation,
        val keyType: SpecificType,
    ) : InferredKeyTypeError, SemanticError

    data class InferredValueTypeResult(
        val valueType: SpecificType,
    )

    data class InconsistentValueTypeError(
        override val location: SourceLocation,
    ) : SemanticError

    override val computedAnalysis = buildAnalysisComputation {
        val associationsAnalyses = associations.map {
            Association.Analysis(
                keyAnalysis = compute(it.key.computedTypeInference) ?: return@buildAnalysisComputation null,
                valueAnalysis = compute(it.value.computedTypeInference) ?: return@buildAnalysisComputation null,
            )
        }

        val distinctiveKeyTypes = associationsAnalyses.map { it.inferredKeyType }.toSet()
        val distinctiveValueTypes = associationsAnalyses.map { it.inferredValueType }.toSet()

        fun analyzeKeys(): Pair<SpecificType, SemanticError?> {
            val keyType = distinctiveKeyTypes.singleOrNull()

            return if (keyType != null) {
                val primitiveKeyType = keyType as? PrimitiveType

                if (primitiveKeyType != null) {
                    Pair(keyType, null)
                } else {
                    Pair(
                        IllType,
                        NonPrimitiveKeyTypeError(
                            location = term.location,
                            keyType = keyType,
                        ),
                    )
                }
            } else {
                Pair(
                    IllType,
                    InconsistentKeyTypeError(
                        location = term.location,
                    ),
                )
            }
        }

        fun analyzeValues(): Pair<SpecificType, SemanticError?> {
            val valueType = distinctiveValueTypes.singleOrNull()

            return if (valueType != null) {
                Pair(valueType, null)
            } else {
                Pair(
                    IllType,
                    InconsistentValueTypeError(
                        location = term.location,
                    ),
                )
            }
        }

        val (keyType, keysError) = analyzeKeys()
        val (valueType, valuesError) = analyzeValues()

        Analysis(
            typeInference = TypeInference(
                inferredType = DictType(
                    keyType = keyType,
                    valueType = valueType,
                ),
            ),
            directErrors = setOfNotNull(
                keysError,
                valuesError,
            ),
        )
    }

    override val subExpressions: Set<Expression>
        get() = SetUtils.unionAllOf(associations) { setOf(it.key, it.value) }

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(associations) { association ->
        Thunk.combine2(
            association.key.bind(
                dynamicScope = dynamicScope,
            ),
            association.value.bind(
                dynamicScope = dynamicScope,
            ),
        ) { key, value ->
            (key as PrimitiveValue) to value
        }
    }.thenJust {
        DictValue.fromMap(
            entries = it.toMap(),
        )
    }
}

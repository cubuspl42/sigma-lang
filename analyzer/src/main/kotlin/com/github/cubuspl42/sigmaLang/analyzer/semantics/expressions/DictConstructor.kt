package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.PrimitiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.utils.SetUtils

class DictConstructor(
    override val outerScope: StaticScope,
    override val term: DictConstructorTerm,
    val associations: List<Association>,
) : Expression() {
    class Association(
        val key: Expression,
        val value: Expression,
    ) {
        data class Analysis(
            val keyAnalysis: Expression.Analysis,
            val valueAnalysis: Expression.Analysis,
        ) {
            val pair: ClassificationContext<Pair<Value, Value>>
                get() = ClassificationContext.transform2(
                    keyAnalysis.classifiedValue,
                    valueAnalysis.classifiedValue,
                ) { key, value ->
                    Thunk.pure((key as PrimitiveValue) to value)
                }

            val inferredKeyType: MembershipType?
                get() = keyAnalysis?.inferredType

            val inferredValueType: MembershipType?
                get() = valueAnalysis?.inferredType
        }

        companion object {
            fun build(
                declarationScope: StaticScope,
                term: DictConstructorTerm.Association,
            ): Association = Association(
                key = Expression.build(
                    outerScope = declarationScope,
                    term = term.key,
                ),
                value = Expression.build(
                    outerScope = declarationScope,
                    term = term.value,
                ),
            )
        }
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: DictConstructorTerm,
        ): DictConstructor = DictConstructor(
            outerScope = outerScope,
            term = term,
            associations = term.associations.map {
                Association.build(
                    declarationScope = outerScope,
                    term = it,
                )
            },
        )
    }

    sealed interface InferredKeyTypeError : SemanticError

    data class InconsistentKeyTypeError(
        override val location: SourceLocation,
    ) : InferredKeyTypeError

    data class NonPrimitiveKeyTypeError(
        override val location: SourceLocation,
        val keyType: MembershipType,
    ) : InferredKeyTypeError, SemanticError

    data class InferredValueTypeResult(
        val valueType: MembershipType,
    )

    data class InconsistentValueTypeError(
        override val location: SourceLocation,
    ) : SemanticError

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val associationsAnalyses = associations.map {
            Association.Analysis(
                keyAnalysis = compute(it.key.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null,
                valueAnalysis = compute(it.value.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null,
            )
        }

        val distinctiveKeyTypes = associationsAnalyses.map { it.inferredKeyType }.toSet()
        val distinctiveValueTypes = associationsAnalyses.map { it.inferredValueType }.toSet()

        fun analyzeKeys(): Pair<MembershipType, SemanticError?> {
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

        fun analyzeValues(): Pair<MembershipType, SemanticError?> {
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

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = DictType(
                    keyType = keyType,
                    valueType = valueType,
                ),
                classifiedValue = ClassificationContext.traverseList(
                    associationsAnalyses,
                ) { it.pair }.transform { pairs ->
                    DictValue(
                        entries = pairs.associate { (key, value) ->
                            key as PrimitiveValue to value
                        }
                    )
                },
            ),
            directErrors = setOfNotNull(
                keysError,
                valuesError,
            ),
        )
    }

    override val subExpressions: Set<Expression> = SetUtils.unionAllOf(associations) { setOf(it.key, it.value) }

    override fun bind(
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
        DictValue(
            entries = it.toMap(),
        )
    }
}

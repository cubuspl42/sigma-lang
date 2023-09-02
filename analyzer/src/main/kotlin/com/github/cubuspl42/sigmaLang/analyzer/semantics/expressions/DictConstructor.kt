package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.PrimitiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorSourceTerm
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

    sealed interface InferredKeyTypeOutcome

    data class InferredKeyTypeResult(
        val keyType: PrimitiveType,
    ) : InferredKeyTypeOutcome

    sealed interface InferredKeyTypeError : InferredKeyTypeOutcome, SemanticError

    data class InconsistentKeyTypeError(
        override val location: SourceLocation,
    ) : InferredKeyTypeError

    data class NonPrimitiveKeyTypeError(
        override val location: SourceLocation,
        val keyType: Type,
    ) : InferredKeyTypeError, SemanticError

    sealed interface InferredValueTypeOutcome

    data class InferredValueTypeResult(
        val valueType: Type,
    ) : InferredValueTypeOutcome

    data class InconsistentValueTypeError(
        override val location: SourceLocation,
    ) : InferredValueTypeOutcome, SemanticError

    private val inferredKeyTypeOutcome: Thunk<InferredKeyTypeOutcome> = Thunk.traverseList(
        associations
    ) {
        it.key.inferredType
    }.thenJust { keyTypes ->
        val distinctiveKeyTypes = keyTypes.toSet()

        val keyType = distinctiveKeyTypes.singleOrNull()

        if (keyType != null) {
            val primitiveKeyType = keyType as? PrimitiveType

            if (primitiveKeyType != null) {
                InferredKeyTypeResult(
                    keyType = keyType,
                )
            } else {
                NonPrimitiveKeyTypeError(
                    location = term.location,
                    keyType = keyType,
                )
            }
        } else {
            InconsistentKeyTypeError(
                location = term.location,
            )
        }
    }

    private val inferredValueTypeOutcome: Thunk<InferredValueTypeOutcome> = Thunk.traverseList(
        associations
    ) {
        it.value.inferredType
    }.thenJust { valueTypes ->
        val distinctiveValueTypes = valueTypes.toSet()

        val valueType = distinctiveValueTypes.singleOrNull()

        if (valueType != null) {
            InferredValueTypeResult(
                valueType = valueType,
            )
        } else {
            InconsistentValueTypeError(
                location = term.location,
            )
        }
    }

    override val inferredType: Thunk<Type> = Thunk.combine2(
        inferredKeyTypeOutcome,
        inferredValueTypeOutcome,
    ) { inferredKeyTypeOutcome, inferredValueTypeOutcome ->
        if (inferredKeyTypeOutcome is InferredKeyTypeResult && inferredValueTypeOutcome is InferredValueTypeResult) {
            DictType(
                keyType = inferredKeyTypeOutcome.keyType,
                valueType = inferredValueTypeOutcome.valueType,
            )
        } else {
            IllType
        }
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

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredKeyTypeOutcome.value as? InferredKeyTypeError,
            inferredValueTypeOutcome.value as? InconsistentValueTypeError,
        )
    }
}

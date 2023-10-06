package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

class UnorderedTupleConstructor(
    override val outerScope: StaticScope,
    override val term: UnorderedTupleConstructorTerm?,
    val entries: Set<Entry>,
) : TupleConstructor() {
    data class Entry(
        val name: Symbol,
        val value: Expression,
    ) {
        data class Analysis(
            val name: Symbol,
            val valueAnalysis: Expression.Analysis,
        ) {
            val inferredValueType: MembershipType
                get() = valueAnalysis.inferredType
        }

        val classifiedEntryValue: ClassificationContext<DictValue.Entry> by lazy {
            value.classifiedValue.transform { value ->
                DictValue.Entry(
                    key = name,
                    value = value,
                )
            }
        }

        companion object {
            fun build(
                declarationScope: StaticScope,
                entry: UnorderedTupleConstructorTerm.Entry,
            ): Entry = Entry(
                name = entry.name,
                value = Expression.build(
                    outerScope = declarationScope,
                    term = entry.value,
                ),
            )
        }
    }

    data class DuplicatedKeyError(
        override val location: SourceLocation?,
        val duplicatedKey: PrimitiveValue,
    ) : SemanticError

    companion object {
        fun build(
            outerScope: StaticScope,
            term: UnorderedTupleConstructorTerm,
        ): UnorderedTupleConstructor = UnorderedTupleConstructor(
            outerScope = outerScope,
            term = term,
            entries = term.entries.map {
                Entry.build(
                    declarationScope = outerScope,
                    entry = it,
                )
            }.toSet(),
        )
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val entriesAnalyses = entries.map {
            Entry.Analysis(
                name = it.name,
                valueAnalysis = compute(it.value.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null,
            )
        }

        val entryTypeByName = entriesAnalyses.groupBy { it.name }.mapValues { (name, entryAnalyses) ->
            entryAnalyses.map { it.inferredValueType }
        }

        val duplicatedKeyErrors = entryTypeByName.entries.mapNotNull { (name, entryTypes) ->
            if (entryTypes.size > 1) {
                DuplicatedKeyError(
                    location = term?.location,
                    duplicatedKey = name,
                )
            } else {
                null
            }
        }

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = UnorderedTupleType(
                    valueTypeByName = entryTypeByName.mapValues { (_, entryTypes) ->
                        entryTypes.singleOrNull() ?: IllType
                    },
                ),
            ),
            directErrors = duplicatedKeyErrors.toSet(),
        )
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.traverseList(entries.toList()) { entry ->
            entry.classifiedEntryValue
        }.transform { entries ->
            DictValue.fromEntries(entries = entries)
        }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(entries.toList()) { entry ->
        entry.value.bind(dynamicScope = dynamicScope).thenJust { entryValue ->
            entry.name to entryValue
        }
    }.thenJust { entries ->
        DictValue(
            entries.toMap(),
        )
    }

    override val subExpressions: Set<Expression> = entries.map { it.value }.toSet()
}

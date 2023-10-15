package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

abstract class UnorderedTupleConstructor : TupleConstructor() {
    abstract override val term: UnorderedTupleConstructorTerm?

    abstract val entries: Set<Entry>

    abstract class Entry {
        abstract val name: Symbol

        abstract val value: Expression

        data class Analysis(
            val name: Symbol,
            val valueAnalysis: Expression.Analysis,
        ) {
            val inferredValueType: MembershipType
                get() = valueAnalysis.inferredType
        }


        companion object {
            fun build(
                context: BuildContext,
                entry: UnorderedTupleConstructorTerm.Entry,
            ): Stub<Entry> = object : Stub<Entry> {
                override val resolved: Entry by lazy {
                    object : Entry() {
                        override val name: Identifier = entry.name

                        override val value: Expression by lazy {
                            Expression.build(
                                context = context,
                                term = entry.value,
                            ).resolved
                        }
                    }
                }
            }
        }
    }

    data class DuplicatedKeyError(
        override val location: SourceLocation?,
        val duplicatedKey: PrimitiveValue,
    ) : SemanticError

    companion object {
        fun build(
            context: BuildContext,
            term: UnorderedTupleConstructorTerm,
        ): Stub<UnorderedTupleConstructor> = object : Stub<UnorderedTupleConstructor> {
            override val resolved: UnorderedTupleConstructor by lazy {
                object : UnorderedTupleConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: UnorderedTupleConstructorTerm = term

                    override val entries: Set<Entry> by lazy {
                        term.entries.map {
                            Entry.build(
                                context = context,
                                entry = it,
                            ).resolved
                        }.toSet()
                    }
                }
            }
        }
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

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.pure(
        DictValue.fromEntries(
            entries = entries.map { entry ->
                DictValue.Entry(
                    key = entry.name,
                    value = entry.value.bind(dynamicScope = dynamicScope),
                )
            },
        )
    )

    override val subExpressions: Set<Expression>
        get() = entries.map { it.value }.toSet()
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
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
            val valueAnalysis: Expression.TypeInference,
        ) {
            val inferredValueType: Type
                get() = valueAnalysis.inferredType as Type
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
        fun Entry(
            name: Symbol,
            value: Expression,
        ): Entry = object : Entry() {
            override val name: Symbol = name

            override val value: Expression = value
        }

        fun Entry(
            nameLazy: Lazy<Symbol>,
            valueLazy: Lazy<Expression>,
        ): Entry = object : Entry() {
            override val name: Symbol by nameLazy

            override val value: Expression by valueLazy
        }

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

    override val computedAnalysis = buildAnalysisComputation {
        val entriesAnalyses = entries.map {
            Entry.Analysis(
                name = it.name,
                valueAnalysis = compute(it.value.computedTypeInference) ?: return@buildAnalysisComputation null,
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

        Analysis(
            typeInference = TypeInference(
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
                    valueThunk = entry.value.bind(dynamicScope = dynamicScope),
                )
            },
        )
    )

    override val subExpressions: Set<Expression>
        get() = entries.map { it.value }.toSet()
}

fun UnorderedTupleConstructor(
    entriesLazy: Lazy<Set<UnorderedTupleConstructor.Entry>>,
): UnorderedTupleConstructor = object : UnorderedTupleConstructor() {
    override val term: UnorderedTupleConstructorTerm? = null

    override val outerScope: StaticScope = StaticScope.Empty

    override val entries: Set<UnorderedTupleConstructor.Entry> by entriesLazy
}

fun UnorderedTupleConstructor(
    entries: Set<UnorderedTupleConstructor.Entry>,
): UnorderedTupleConstructor = UnorderedTupleConstructor(
    entriesLazy = lazyOf(entries),
)

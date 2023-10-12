package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

abstract class UnorderedTupleTypeConstructor : TupleTypeConstructor() {
    abstract override val term: UnorderedTupleTypeConstructorTerm

    abstract val entries: Set<Entry>

    abstract class Entry {
        abstract val name: Symbol

        abstract val type: Expression

        data class Analysis(
            val name: Symbol,
            val typeAnalysis: Expression.Analysis,
        )

        companion object {
            fun build(
                context: BuildContext,
                entry: UnorderedTupleTypeConstructorTerm.Entry,
            ): Stub<Entry> {
                val typeStub = Expression.build(
                    context = context,
                    term = entry.type,
                )

                return object : Stub<Entry> {
                    override val resolved: Entry by lazy {
                        object : Entry() {
                            override val name: Symbol = entry.name

                            override val type: Expression by lazy { typeStub.resolved }
                        }
                    }
                }
            }
        }

        val classifiedEntry: ClassificationContext<UnorderedTupleType.Entry>
            get() = type.classifiedValue.transformThunk { typeValueThunk ->
                Thunk.pure(
                    UnorderedTupleType.Entry(
                        name = name,
                        typeThunk = typeValueThunk.thenJust { it.asType!! },
                    )
                )
            }
    }

    companion object {
        fun build(
            context: BuildContext,
            term: UnorderedTupleTypeConstructorTerm,
        ): Stub<UnorderedTupleTypeConstructor> {
            val entryStubs = term.entries.map {
                Entry.build(
                    context = context,
                    entry = it,
                ).resolved
            }

            return object : Stub<UnorderedTupleTypeConstructor> {
                override val resolved: UnorderedTupleTypeConstructor by lazy {
                    object : UnorderedTupleTypeConstructor() {
                        override val outerScope: StaticScope = context.outerScope

                        override val term: UnorderedTupleTypeConstructorTerm = term

                        override val entries: Set<Entry> by lazy { entryStubs.toSet() }
                    }
                }
            }
        }
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.traverseList(entries.toList()) { entry ->
            entry.classifiedEntry
        }.transform { entries ->
            UnorderedTupleType.fromEntries(
                entries = entries,
            ).asValue
        }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.pure(
        object : UnorderedTupleType() {
            override val valueTypeThunkByName = entries.associate {
                val entryType = it.type.bind(
                    dynamicScope = dynamicScope,
                ).thenJust { entryType ->
                    entryType.asType!!
                }

                it.name to entryType
            }
        }.asValue
    )

    override val subExpressions: Set<Expression>
        get() = entries.map { it.type }.toSet()
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm

class UnorderedTupleTypeConstructor(
    override val outerScope: StaticScope,
    override val term: UnorderedTupleTypeConstructorTerm,
    val entries: Set<Entry>,
) : TupleTypeConstructor() {
    data class Entry(
        val name: Symbol,
        val type: Expression,
    ) {
        data class Analysis(
            val name: Symbol,
            val typeAnalysis: Expression.Analysis,
        )

        companion object {
            fun build(
                declarationScope: StaticScope,
                entry: UnorderedTupleTypeConstructorTerm.Entry,
            ): Entry = Entry(
                name = entry.name,
                type = Expression.build(
                    outerScope = declarationScope,
                    term = entry.type,
                ),
            )
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
            outerScope: StaticScope,
            term: UnorderedTupleTypeConstructorTerm,
        ): UnorderedTupleTypeConstructor = UnorderedTupleTypeConstructor(
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

    override val subExpressions: Set<Expression> = entries.map { it.type }.toSet()
}

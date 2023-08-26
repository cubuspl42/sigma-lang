package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm
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

    override val inferredType: Thunk<Type> = MetaType.asThunk

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = Thunk.traverseList(entries.toList()) { entry ->
        entry.type.bind(
            scope = scope,
        ).thenJust { entryType ->
            entry.name to (entryType as Type)
        }
    }.thenJust { entries ->
        UnorderedTupleType(
            valueTypeByName = entries.toMap(),
        )
    }

    override val subExpressions: Set<Expression> = entries.map { it.type }.toSet()

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}

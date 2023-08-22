package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm

class UnorderedTupleTypeConstructor(
    override val outerScope: StaticScope,
    override val term: UnorderedTupleTypeConstructorSourceTerm,
    val entries: Set<UnorderedTupleConstructor.Entry>,
) : TupleTypeConstructor() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: UnorderedTupleTypeConstructorSourceTerm,
        ): UnorderedTupleTypeConstructor = UnorderedTupleTypeConstructor(
            outerScope = outerScope,
            term = term,
            entries = term.entries.map {
                UnorderedTupleConstructor.Entry.build(
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
        entry.value.bind(
            scope = scope,
        ).thenJust { entryType ->
            entry.name to (entryType as Type)
        }

    }.thenJust { entries ->
        UnorderedTupleType(
            valueTypeByName = entries.toMap(),
        )
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}

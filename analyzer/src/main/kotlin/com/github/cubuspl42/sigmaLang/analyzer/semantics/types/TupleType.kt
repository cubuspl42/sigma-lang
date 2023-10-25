package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk

abstract class TupleType : TableType() {

    abstract class Entry {
        abstract val key: PrimitiveValue

        abstract val name: Symbol?

        abstract val typeThunk: Thunk<TypeAlike>

        val type: TypeAlike
            get() = typeThunk.value ?: throw IllegalStateException("Unable to evaluate the type thunk")
    }

    abstract fun getTypeByKey(key: PrimitiveValue): TypeAlike?

    abstract val entries: Collection<Entry>

    abstract override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>
}

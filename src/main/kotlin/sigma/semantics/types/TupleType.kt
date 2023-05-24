package sigma.semantics.types

import sigma.SyntaxValueScope
import sigma.semantics.expressions.Abstraction
import sigma.evaluation.values.Symbol

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
data class TupleType(
    val orderedEntries: List<OrderedEntry>,
    val unorderedEntries: Set<UnorderedEntry>,
) : TableType() {
    sealed interface Entry {
        fun dump(): String = listOfNotNull(
            name?.let { "$it:" },
            type.dump(),
        ).joinToString(" ")

        val name: Symbol?

        val type: Type
    }

    data class OrderedEntry(
        val index: Int,
        override val name: Symbol?,
        override val type: Type,
    ) : Entry

    data class UnorderedEntry(
        override val name: Symbol,
        override val type: Type,
    ) : Entry

    companion object {
        val Empty = TupleType(
            orderedEntries = emptyList(),
            unorderedEntries = emptySet(),
        )

        fun ordered(vararg entries: OrderedEntry): TupleType = TupleType(
            orderedEntries = entries.toList(),
            unorderedEntries = emptySet(),
        )

        fun unordered(vararg entries: UnorderedEntry): TupleType = TupleType(
            orderedEntries = emptyList(), unorderedEntries = entries.toSet()
        )
    }

    val entries: Set<Entry> by lazy {
        (orderedEntries + unorderedEntries).toSet()
    }

    override fun dump(): String {
        fun dumpEntries(entries: Collection<Entry>): String? = when {
            entries.isEmpty() -> null
            else -> entries.joinToString(", ") { it.dump() }
        }

        val dumpedEntries = listOfNotNull(
            dumpEntries(orderedEntries)?.let { "($it)" },
            dumpEntries(unorderedEntries),
        ).joinToString(", ")

        return "{$dumpedEntries}"
    }

    fun getFieldTypeByName(key: Symbol): Type? {
        val fieldEntry = entries.firstOrNull { it.name == key }
        return fieldEntry?.type
    }

    fun getFieldTypeByIndex(index: Int): Type? = orderedEntries.getOrNull(index)?.type

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = entries.isEmpty()

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        if (assignedType !is TupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-tuple is assigned",
        )

        val orderedEntriesResolution = orderedEntries.fold(
            initial = TypeVariableResolution.Empty,
        ) { accumulatedResolution, orderedEntry ->
            val index = orderedEntry.index

            val assignedValueType = assignedType.getFieldTypeByIndex(index) ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple lacks element at index $index",
            )

            val typeResolution = orderedEntry.type.resolveTypeVariables(
                assignedType = assignedValueType,
            )

            accumulatedResolution.mergeWith(typeResolution)
        }

        val unorderedEntriesResolution = unorderedEntries.fold(
            initial = TypeVariableResolution.Empty,
        ) { accumulatedResolution, unorderedEntry ->
            val name = unorderedEntry.name

            val assignedValueType = assignedType.getFieldTypeByName(name) ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple lacks key $name",
            )

            val typeResolution = unorderedEntry.type.resolveTypeVariables(
                assignedType = assignedValueType,
            )

            accumulatedResolution.mergeWith(typeResolution)
        }

        return orderedEntriesResolution.mergeWith(unorderedEntriesResolution)
    }

    fun toStaticValueScope(): SyntaxValueScope = object : SyntaxValueScope {
        override fun getValueType(valueName: Symbol): Type? = getFieldTypeByName(key = valueName)
    }

    fun toArgumentDeclarationBlock(): Abstraction.ArgumentDeclarationBlock = Abstraction.ArgumentDeclarationBlock(
        argumentDeclarations = entries.mapNotNull { entry ->
            entry.name?.let { name ->
                Abstraction.ArgumentDeclaration(
                    name = name,
                    type = entry.type,
                )
            }
        },
    )

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType = TupleType(
        orderedEntries = orderedEntries.map {
            it.copy(
                type = it.type.substituteTypeVariables(
                    resolution = resolution,
                ),
            )
        },
        unorderedEntries = unorderedEntries.map {
            it.copy(
                type = it.type.substituteTypeVariables(
                    resolution = resolution,
                ),
            )
        }.toSet(),
    )
}

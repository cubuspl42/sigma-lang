package sigma

import sigma.parser.antlr.SigmaParser
import kotlin.String

data class Dict(
    val label: String? = null,
    val entries: Map<Value, Expression>,
) : Value() {
    data class Entry(
        val key: Expression,
        val value: Expression,
    ) {
        fun dump(): String = "${key.dump()}: ${value.dump()}"
    }

    companion object {
        val empty = Dict(
            entries = emptyMap(),
        )

        fun build(
            form: SigmaParser.DictContext,
        ): Dict = Dict(
            label = form.label?.text,
            entries = form.entry().associate {
                Value.build(it.argument) to Expression.build(it.image)
            },
        )
    }

    override fun apply(
        scope: Scope,
        key: Value,
    ): Value {
        val value = entries[key] ?: throw IllegalStateException("Dict @${label} doesn't have key ${key.dump()}")

        val extendedScope = when {
            label != null -> scope.extend(
                label = label,
                value = this,
            )

            else -> scope
        }

        return value.evaluate(scope = extendedScope)
    }

    override fun dump(): String = "{${
        entries.entries.joinToString {
            it.key.dump() + ": " + it.value.dump()
        }
    }}"
}

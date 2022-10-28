package sigma

import sigma.values.BoolValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.Table
import sigma.values.UndefinedValue
import sigma.values.Value

object IntegerTable : Table() {
    override fun read(argument: Value): Value? {
        val symbol = argument as? Symbol
        val integer = symbol?.name?.toIntOrNull()

        return integer?.let { IntValue(it) }
    }

    override fun dumpContent(): String = "(integer table)"
}

private object BuiltinContext : Table() {
    private val builtins = mapOf(
        Symbol.of("false") to BoolValue.False,
        Symbol.of("true") to BoolValue.True,
        Symbol.of("if") to BoolValue.If,
        Symbol.of("mul") to IntValue.Mul,
        Symbol.of("div") to IntValue.Div,
        Symbol.of("add") to IntValue.Add,
        Symbol.of("sub") to IntValue.Sub,
        Symbol.of("sq") to IntValue.Sq,
        Symbol.of("eq") to IntValue.Eq,
        Symbol.of("lt") to IntValue.Lt,
        Symbol.of("lte") to IntValue.Lte,
        Symbol.of("gt") to IntValue.Gt,
        Symbol.of("gte") to IntValue.Gte,
        Symbol.of("link") to Link,
        Symbol.of("isUndefined") to UndefinedValue.IsUndefined,
    )

    override fun read(
        argument: Value,
    ): Value? = when (argument) {
        !is Symbol -> null
        else -> builtins[argument]
    }

    override fun dumpContent(): String = "(built-in context)"
}

val GlobalContext: Table = BuiltinContext.chainWith(IntegerTable)

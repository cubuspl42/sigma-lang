package sigma

object IntegerTable : Table() {
    override fun read(argument: Value): Value? {
        val symbol = argument as? Symbol
        val integer = symbol?.name?.toIntOrNull()

        return integer?.let { IntValue(it) }
    }

    override fun dumpContent(): String = "0 ~> ${Int.MAX_VALUE}"

    override fun isSubsetOf(other: FunctionValue): Boolean {
        return other is IntegerTable
    }
}

private val BuiltinContext = DictAssociativeTable(
    environment = EmptyTable,
    associations = ExpressionTable(
        entries = mapOf(
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
            Symbol.of("link") to FunctionValue.Link,
            Symbol.of("isUndefined") to UndefinedValue.IsUndefined,
        ),
    ),
)

val GlobalContext = BuiltinContext.chainWith(IntegerTable)

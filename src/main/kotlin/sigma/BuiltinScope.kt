package sigma

import sigma.types.BoolType
import sigma.types.AbstractionType
import sigma.types.IntCollectiveType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.types.UnorderedTupleType
import sigma.values.BoolValue
import sigma.values.FunctionValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.UndefinedValue
import sigma.values.Value
import sigma.values.tables.Scope

interface BuiltinValue {
    val type: Type
    val value: Value
}

object BuiltinScope : StaticValueScope, Scope {
    private data class SimpleBuiltinValue(
        override val type: Type,
        override val value: Value,
    ) : BuiltinValue

    private val builtinValues: Map<Symbol, BuiltinValue> = mapOf(
        Symbol.of("false") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(false),
        ),
        Symbol.of("true") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(true),
        ),
        Symbol.of("if") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = AbstractionType(
                    // TODO: Improve this typing
                    argumentType = UnorderedTupleType.Empty,
                    imageType = UndefinedType,
                ),
            ),
            value = BoolValue.If,
        ),
        Symbol.of("mul") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Mul,
        ),
        Symbol.of("div") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Div,
        ),
        Symbol.of("add") to SimpleBuiltinValue(
            type = AbstractionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Add,
        ),
        Symbol.of("sub") to SimpleBuiltinValue(
            type = AbstractionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Sub,
        ),
        Symbol.of("sq") to SimpleBuiltinValue(
            type = AbstractionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Sq,
        ),
        Symbol.of("eq") to SimpleBuiltinValue(
            type = AbstractionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Eq,
        ),
        Symbol.of("lt") to SimpleBuiltinValue(
            type = AbstractionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lt,
        ),
        Symbol.of("lte") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lte,
        ),
        Symbol.of("gt") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gt,
        ),
        Symbol.of("gte") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gte,
        ),
        Symbol.of("link") to SimpleBuiltinValue(
            type = AbstractionType(
                // TODO: Improve this typing
                argumentType = UnorderedTupleType.Empty,
                imageType = UndefinedType,
            ),
            value = FunctionValue.Link,
        ),
        Symbol.of("isUndefined") to SimpleBuiltinValue(
            type = AbstractionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = UndefinedValue.IsUndefined,
        ),
        Symbol.of("chunked4") to FunctionValue.Chunked4,
        Symbol.of("dropFirst") to FunctionValue.DropFirst,
        Symbol.of("windows") to FunctionValue.Windows,
        Symbol.of("take") to FunctionValue.Take,
        Symbol.of("map") to FunctionValue.MapFn,
        Symbol.of("sum") to FunctionValue.Sum,
    )

    override fun getValueType(
        valueName: Symbol,
    ): Type? = getBuiltin(
        name = valueName,
    )?.type

    override fun get(
        name: Symbol,
    ): Value? = getBuiltin(
        name = name,
    )?.value

    private fun getBuiltin(
        name: Symbol,
    ): BuiltinValue? = builtinValues[name]
}

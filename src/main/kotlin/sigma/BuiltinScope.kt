package sigma

import sigma.types.BoolType
import sigma.types.AbstractionType
import sigma.types.IntCollectiveType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.values.BoolValue
import sigma.values.FunctionValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.UndefinedValue
import sigma.values.Value
import sigma.values.tables.Scope

object BuiltinScope : StaticValueScope, Scope {
    private data class BuiltinValue(
        val type: Type,
        val value: Value,
    )

    private val builtinValues: Map<Symbol, BuiltinValue> = mapOf(
        Symbol.of("false") to BuiltinValue(
            type = BoolType,
            value = BoolValue(false),
        ),
        Symbol.of("true") to BuiltinValue(
            type = BoolType,
            value = BoolValue(true),
        ),
        Symbol.of("if") to BuiltinValue(
            type = AbstractionType(
                imageType = AbstractionType(
                    // TODO: Improve this typing
                    imageType = UndefinedType,
                ),
            ),
            value = BoolValue.If,
        ),
        Symbol.of("mul") to BuiltinValue(
            type = AbstractionType(imageType = IntCollectiveType),
            value = IntValue.Mul,
        ),
        Symbol.of("div") to BuiltinValue(
            type = AbstractionType(imageType = IntCollectiveType),
            value = IntValue.Div,
        ),
        Symbol.of("add") to BuiltinValue(
            type = AbstractionType(imageType = IntCollectiveType),
            value = IntValue.Add,
        ),
        Symbol.of("sub") to BuiltinValue(
            type = AbstractionType(imageType = IntCollectiveType),
            value = IntValue.Sub,
        ),
        Symbol.of("sq") to BuiltinValue(
            type = AbstractionType(imageType = IntCollectiveType),
            value = IntValue.Sq,
        ),
        Symbol.of("eq") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = IntValue.Eq,
        ),
        Symbol.of("lt") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = IntValue.Lt,
        ),
        Symbol.of("lte") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = IntValue.Lte,
        ),
        Symbol.of("gt") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = IntValue.Gt,
        ),
        Symbol.of("gte") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = IntValue.Gte,
        ),
        Symbol.of("link") to BuiltinValue(
            // TODO: Improve this typing
            type = AbstractionType(imageType = UndefinedType),
            value = FunctionValue.Link,
        ),
        Symbol.of("isUndefined") to BuiltinValue(
            type = AbstractionType(imageType = BoolType),
            value = UndefinedValue.IsUndefined,
        ),
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

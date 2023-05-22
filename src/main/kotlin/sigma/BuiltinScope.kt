package sigma

import sigma.semantics.BuiltinDefinition
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UndefinedType
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.scope.Scope

interface BuiltinValue {
    val type: Type
    val value: Value
}

object BuiltinScope : SyntaxValueScope, Scope, DeclarationScope {
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
            type = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("guard"),
                            type = BoolType,
                        ),
                    ),
                ),
                imageType = UniversalFunctionType(
                    argumentType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Symbol.of("then") to TypeVariable,
                            Symbol.of("else") to TypeVariable,
                        )
                    ),
                    imageType = TypeVariable,
                ),
            ),
            value = BoolValue.If,
        ),
        Symbol.of("mul") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Mul,
        ),
        Symbol.of("div") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Div,
        ),
        Symbol.of("add") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Add,
        ),
        Symbol.of("sub") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Sub,
        ),
        Symbol.of("sq") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = null,
                            type = IntCollectiveType,
                        ),
                    ),
                ),
                imageType = IntCollectiveType,
            ),
            value = IntValue.Sq,
        ),
        Symbol.of("eq") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Eq,
        ),
        Symbol.of("lt") to SimpleBuiltinValue(
            type = UniversalFunctionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lt,
        ),
        Symbol.of("lte") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lte,
        ),
        Symbol.of("gt") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gt,
        ),
        Symbol.of("gte") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gte,
        ),
        Symbol.of("link") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                // TODO: Improve this typing
                argumentType = UnorderedTupleType.Empty,
                imageType = UndefinedType,
            ),
            value = FunctionValue.Link,
        ),
        Symbol.of("chunked4") to FunctionValue.Chunked4,
        Symbol.of("dropFirst") to FunctionValue.DropFirst,
        Symbol.of("windows") to FunctionValue.Windows,
        Symbol.of("take") to FunctionValue.Take,
        Symbol.of("map") to FunctionValue.MapFn,
        Symbol.of("sum") to FunctionValue.Sum,
        Symbol.of("product") to FunctionValue.Product,
        Symbol.of("max") to FunctionValue.Max,
        Symbol.of("length") to FunctionValue.LengthFunction,
        Symbol.of("concat") to FunctionValue.ConcatFunction,
    )

    private val builtinDefinitions = builtinValues.entries.associate { (name, builtinValue) ->
        name to BuiltinDefinition(
            name = name,
            type = builtinValue.type,
            value = builtinValue.value,
        )
    }

    override fun getValueType(
        valueName: Symbol,
    ): Type? = getBuiltin(
        name = valueName,
    )?.type

    override fun getValue(
        name: Symbol,
    ): Value? = getBuiltin(
        name = name,
    )?.value

    private fun getBuiltin(
        name: Symbol,
    ): BuiltinValue? = builtinValues[name]

    override fun resolveDeclaration(name: Symbol): BuiltinDefinition? = builtinDefinitions[name]
}

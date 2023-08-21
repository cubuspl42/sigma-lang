package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType

interface BuiltinValue {
    val type: Type
    val value: Value
}

private class BuiltinValueDefinition(
    override val name: Symbol,
    val value: Value,
    val type: Type,
) : StaticDefinition() {
//    override val effectiveValueType: Computation<Type> = Computation.pure(type)

    override val staticValue: Thunk<Value> = value.asThunk
    override val errors: Set<SemanticError>
        get() = emptySet()

    val asResolvedName: ResolvedName
        get() = ResolvedName(
            type = type.asThunk,
            resolution = BuiltinResolution(
                builtinValue = value,
            ),
        )
}

object BuiltinScope : Scope, StaticScope {
    data class SimpleBuiltinValue(
        override val type: Type,
        override val value: Value,
    ) : BuiltinValue

    private val builtinValues: Map<Symbol, BuiltinValue> = mapOf(
        Symbol.of("Bool") to SimpleBuiltinValue(
            type = MetaType,
            value = BoolType,
        ),
        Symbol.of("Int") to SimpleBuiltinValue(
            type = MetaType,
            value = IntCollectiveType,
        ),
        Symbol.of("Type") to SimpleBuiltinValue(
            type = MetaType,
            value = MetaType,
        ),
        Symbol.of("Set") to SetType.constructor,
        Symbol.of("setOf") to SetType.setOf,
        Symbol.of("setContains") to SetType.setContains,
        Symbol.of("setUnion") to SetType.setUnion,
        Symbol.of("emptySet") to SetType.emptySet,
        Symbol.of("setSum") to SetType.SetSum,
        Symbol.of("false") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(false),
        ),
        Symbol.of("true") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(true),
        ),

        Symbol.of("not") to BoolValue.Not,
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
                            Symbol.of("then") to TypeVariable(
                                formula = Formula.of("r"),
                            ),
                            Symbol.of("else") to TypeVariable(
                                formula = Formula.of("r"),
                            ),
                        )
                    ),
                    imageType = TypeVariable(
                        formula = Formula.of("r"),
                    ),
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
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("primary") to DictType(
                            keyType = TypeVariable(
                                formula = Formula.of("K"),
                            ),
                            valueType = TypeVariable(
                                formula = Formula.of("V"),
                            ),
                        ),
                        Symbol.of("secondary") to DictType(
                            keyType = TypeVariable(
                                formula = Formula.of("K"),
                            ),
                            valueType = TypeVariable(
                                formula = Formula.of("V"),
                            ),
                        ),
                    )
                ),
                imageType = DictType(
                    keyType = TypeVariable(
                        formula = Formula.of("K"),
                    ),
                    valueType = TypeVariable(
                        formula = Formula.of("V"),
                    ),
                ),
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

    private val builtinValueDeclarations = builtinValues.map { (name, builtinValue) ->
        BuiltinValueDefinition(
            name = name,
            value = builtinValue.value,
            type = builtinValue.type,
        )
    }.toSet()

    private val builtinDeclarations: Map<Symbol, BuiltinValueDefinition> =
        builtinValueDeclarations.associateBy { it.name }

    val names: Set<Symbol>
        get() = builtinDeclarations.keys

    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = getBuiltin(
        name = name,
    )?.value?.asThunk

    private fun getBuiltin(
        name: Symbol,
    ): BuiltinValue? = builtinValues[name]

    override fun resolveName(
        name: Symbol,
    ): ResolvedName? = builtinDeclarations[name]?.asResolvedName
}

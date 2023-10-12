package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UndefinedType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue

interface BuiltinValue {
    val type: MembershipType
    val value: Value
}

private class BuiltinDefinition(
    override val name: Identifier,
    val value: Value,
    val type: MembershipType,
) : ConstantDefinition() {
    override val valueThunk: Thunk<Value> = value.toThunk()

    override val computedEffectiveType = Expression.Computation.pure(type)
}

object BuiltinScope : DynamicScope, StaticScope {
    data class SimpleBuiltinValue(
        override val type: MembershipType,
        override val value: Value,
    ) : BuiltinValue

    private val builtinValues: Map<Identifier, BuiltinValue> = mapOf(
        Identifier.of("Bool") to SimpleBuiltinValue(
            type = TypeType,
            value = BoolType.asValue,
        ),
        Identifier.of("Int") to SimpleBuiltinValue(
            type = TypeType,
            value = IntCollectiveType.asValue,
        ),
        Identifier.of("String") to SimpleBuiltinValue(
            type = TypeType,
            value = StringType.asValue,
        ),
        Identifier.of("Type") to SimpleBuiltinValue(
            type = TypeType,
            value = TypeType.asValue,
        ),
        Identifier.of("Undefined") to SimpleBuiltinValue(
            type = TypeType,
            value = UndefinedType.asValue,
        ),
        Identifier.of("Set") to SetType.constructor,
        Identifier.of("setOf") to SetType.setOf,
        Identifier.of("setContains") to SetType.setContains,
        Identifier.of("setUnion") to SetType.setUnion,
        Identifier.of("emptySet") to SetType.emptySet,
        Identifier.of("setSum") to SetType.SetSum,
        Identifier.of("false") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(false),
        ),
        Identifier.of("true") to SimpleBuiltinValue(
            type = BoolType,
            value = BoolValue(true),
        ),

        Identifier.of("not") to BoolValue.Not,
        Identifier.of("if") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Identifier.of("guard"),
                            type = BoolType,
                        ),
                    ),
                ),
                imageType = UniversalFunctionType(
                    argumentType = UnorderedTupleType(
                        valueTypeByName = mapOf(
                            Identifier.of("then") to TypeVariable(
                                formula = Formula.of("r"),
                            ),
                            Identifier.of("else") to TypeVariable(
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
        Identifier.of("mul") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Mul,
        ),
        Identifier.of("div") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Div,
        ),
        Identifier.of("add") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Add,
        ),
        Identifier.of("sub") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = IntCollectiveType,
            ),
            value = IntValue.Sub,
        ),
        Identifier.of("sq") to SimpleBuiltinValue(
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
        Identifier.of("eq") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Eq,
        ),
        Identifier.of("lt") to SimpleBuiltinValue(
            type = UniversalFunctionType(

                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lt,
        ),
        Identifier.of("lte") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Lte,
        ),
        Identifier.of("gt") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gt,
        ),
        Identifier.of("gte") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType.Empty,
                imageType = BoolType,
            ),
            value = IntValue.Gte,
        ),
        Identifier.of("link") to SimpleBuiltinValue(
            type = UniversalFunctionType(
                argumentType = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("primary") to DictType(
                            keyType = TypeVariable(
                                formula = Formula.of("K"),
                            ),
                            valueType = TypeVariable(
                                formula = Formula.of("V"),
                            ),
                        ),
                        Identifier.of("secondary") to DictType(
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
        Identifier.of("chunked4") to FunctionValue.Chunked4,
        Identifier.of("dropFirst") to FunctionValue.DropFirst,
        Identifier.of("windows") to FunctionValue.Windows,
        Identifier.of("take") to FunctionValue.Take,
        Identifier.of("map") to FunctionValue.MapFn,
        Identifier.of("sum") to FunctionValue.Sum,
        Identifier.of("product") to FunctionValue.Product,
        Identifier.of("max") to FunctionValue.Max,
        Identifier.of("length") to FunctionValue.LengthFunction,
        Identifier.of("concat") to FunctionValue.ConcatFunction,
        Identifier.of("is") to ClassDefinition.Is,
    )

    private val builtinValueDeclarations = builtinValues.map { (name, builtinValue) ->
        BuiltinDefinition(
            name = name,
            value = builtinValue.value,
            type = builtinValue.type,
        )
    }.toSet()

    private val builtinDeclarations: Map<Identifier, BuiltinDefinition> = builtinValueDeclarations.associateBy { it.name }

    val names: Set<Identifier>
        get() = builtinDeclarations.keys

    override fun getValue(
        name: Identifier,
    ): Thunk<Value>? = getBuiltin(
        name = name,
    )?.value?.toThunk()

    private fun getBuiltin(
        name: Identifier,
    ): BuiltinValue? = builtinValues[name]

    override fun resolveName(
        name: Identifier,
    ): ClassifiedIntroduction? = builtinDeclarations[name]

    override fun getAllNames(): Set<Identifier> = builtinDeclarations.keys
}

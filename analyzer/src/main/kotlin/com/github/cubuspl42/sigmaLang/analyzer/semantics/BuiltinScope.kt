package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.QuasiExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
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
    override val name: Symbol,
    val value: Value,
    val type: MembershipType,
) : ConstantDefinition() {
    override val body: QuasiExpression = object : QuasiExpression() {
        override val computedAnalysis: Expression.Computation<Expression.Analysis?> = Expression.Computation.pure(
            Expression.Analysis(inferredType = type),
        )

        override val classifiedValue: ClassificationContext<Value> = ConstClassificationContext.pure(value)
    }
}

object BuiltinScope : DynamicScope, StaticScope {
    data class SimpleBuiltinValue(
        override val type: MembershipType,
        override val value: Value,
    ) : BuiltinValue

    private val builtinValues: Map<Symbol, BuiltinValue> = mapOf(
        Symbol.of("Bool") to SimpleBuiltinValue(
            type = TypeType,
            value = BoolType.asValue,
        ),
        Symbol.of("Int") to SimpleBuiltinValue(
            type = TypeType,
            value = IntCollectiveType.asValue,
        ),
        Symbol.of("String") to SimpleBuiltinValue(
            type = TypeType,
            value = StringType.asValue,
        ),
        Symbol.of("Type") to SimpleBuiltinValue(
            type = TypeType,
            value = TypeType.asValue,
        ),
        Symbol.of("Undefined") to SimpleBuiltinValue(
            type = TypeType,
            value = UndefinedType.asValue,
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
        Symbol.of("is") to ClassDefinition.Is,
    )

    private val builtinValueDeclarations = builtinValues.map { (name, builtinValue) ->
        BuiltinDefinition(
            name = name,
            value = builtinValue.value,
            type = builtinValue.type,
        )
    }.toSet()

    private val builtinDeclarations: Map<Symbol, BuiltinDefinition> = builtinValueDeclarations.associateBy { it.name }

    val names: Set<Symbol>
        get() = builtinDeclarations.keys

    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = getBuiltin(
        name = name,
    )?.value?.toThunk()

    private fun getBuiltin(
        name: Symbol,
    ): BuiltinValue? = builtinValues[name]

    override fun resolveName(
        name: Symbol,
    ): Introduction? = builtinDeclarations[name]

    override fun getAllNames(): Set<Symbol> = builtinDeclarations.keys
}

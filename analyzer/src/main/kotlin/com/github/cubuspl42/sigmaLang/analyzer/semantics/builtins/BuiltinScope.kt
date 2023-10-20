package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SetType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UndefinedType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

interface BuiltinValue {
    val type: MembershipType
    val value: Value
}


class Builtin(
    type: MembershipType,
    private val value: Value,
) : Expression() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ExpressionTerm? = null

    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = Computation.pure(
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = type,
            ),
            directErrors = emptySet(),
        )
    )

    override val subExpressions: Set<Expression>
        get() = emptySet()

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(value)
}

private class BuiltinDefinition(
    override val name: Symbol,
    val value: Value,
    val type: MembershipType,
) : Definition {
//    override val valueThunk: Thunk<Value> = value.toThunk()

    override val computedBodyType: Expression.Computation<TypeAlike> = Expression.Computation.pure(type)

    override val bodyStub: Expression.Stub<Expression> = Expression.Stub.of(
        Builtin(
            type = type,
            value = value,
        )
    )
}

object BuiltinScope : StaticScope {
    data class SimpleBuiltinValue(
        override val type: MembershipType,
        override val value: Value,
    ) : BuiltinValue

    private val builtinValues: Map<Symbol, BuiltinValue> = mapOf(
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
        Identifier.of("if") to IfFunction,
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
        Identifier.of("link") to LinkFunction,
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
        ArrayTypeConstructor.Name to ArrayTypeConstructor,
        DictTypeConstructor.Name to DictTypeConstructor,
    )

    private val builtinValueDeclarations = builtinValues.map { (name, builtinValue) ->
        BuiltinDefinition(
            name = name,
            value = builtinValue.value,
            type = builtinValue.type,
        )
    }.toSet()

    private val builtinDeclarations: Map<Symbol, BuiltinDefinition> = builtinValueDeclarations.associateBy { it.name }

    private fun getBuiltin(
        name: Symbol,
    ): BuiltinValue? = builtinValues[name]

    override fun resolveName(
        name: Symbol,
    ): Introduction? = builtinDeclarations[name]

    override fun getAllNames(): Set<Symbol> = builtinDeclarations.keys
}

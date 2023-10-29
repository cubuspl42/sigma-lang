package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.Chunked4Function
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.ConcatFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.DropFirstFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.LengthFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.MapFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.math.MaxFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.math.ProductFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.math.SumFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.TakeFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.WindowsFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set.SetConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set.SetContainsFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set.SetOfFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set.SetSum
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections.set.SetUnionFunction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UndefinedType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue

object BuiltinScope : StaticScope {
    data class SimpleBuiltinValue(
        override val type: Type,
        val value: Value,
    ) : AtomicExpression() {
        override val valueThunk: Thunk<Value> by lazy { Thunk.pure(value) }
    }

    private val builtinValues: Map<Symbol, Expression> = mapOf(
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
        Identifier.of("Set") to SetConstructor,
        Identifier.of("setOf") to SetOfFunction,
        Identifier.of("setContains") to SetContainsFunction,
        Identifier.of("setUnion") to SetUnionFunction,
        Identifier.of("emptySet") to EmptySetFunction,
        Identifier.of("setSum") to SetSum,
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
        Identifier.of("chunked4") to Chunked4Function,
        Identifier.of("dropFirst") to DropFirstFunction,
        Identifier.of("windows") to WindowsFunction,
        Identifier.of("take") to TakeFunction,
        Identifier.of("map") to MapFunction,
        Identifier.of("sum") to SumFunction,
        Identifier.of("product") to ProductFunction,
        Identifier.of("max") to MaxFunction,
        Identifier.of("length") to LengthFunction,
        Identifier.of("concat") to ConcatFunction,
        Identifier.of("is") to ClassDefinition.Is,
        ArrayTypeConstructor.Name to ArrayTypeConstructor,
        DictTypeConstructor.Name to DictTypeConstructor,
    )

    private val builtinDeclarations: Map<Symbol, Definition> = builtinValues.mapValues { (_, builtinValue) ->
        Definition(
            body = builtinValue,
        )
    }

    override fun resolveNameLeveled(
        name: Symbol,
    ): LeveledResolvedIntroduction? = builtinDeclarations[name]?.let {
        LeveledResolvedIntroduction(
            level = StaticScope.Level.Meta,
            resolvedIntroduction = ResolvedDefinition(definition = it),
        )
    }

    override fun getAllNames(): Set<Symbol> = builtinDeclarations.keys
}

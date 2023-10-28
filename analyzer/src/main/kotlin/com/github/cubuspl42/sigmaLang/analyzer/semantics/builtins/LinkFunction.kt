package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType

object LinkFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = orderedTraitDeclaration(
        Identifier.of("k"),
        Identifier.of("v"),
    )

    private val kTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )


    private val vTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )


    override val body = object : BuiltinFunctionConstructor() {
        override val argumentType: TableType = UnorderedTupleType.fromEntries(
            entries = listOf(
                UnorderedTupleType.NamedEntry(
                    name = Identifier.of("primary"),
                    type = DictType(
                        keyType = kTypeVariable,
                        valueType = vTypeVariable,
                    ),
                ),
                UnorderedTupleType.NamedEntry(
                    name = Identifier.of("secondary"),
                    type = DictType(
                        keyType = kTypeVariable,
                        valueType = vTypeVariable,
                    ),
                ),
            ),
        )

        override val imageType = DictType(
            keyType = kTypeVariable,
            valueType = vTypeVariable,
        )

        override val function: FunctionValue = object : FunctionValue() {
            override fun apply(argument: Value): Thunk<Value> {
                argument as FunctionValue

                return Thunk.combine2(
                    argument.apply(
                        argument = Identifier.of("primary"),
                    ), argument.apply(
                        argument = Identifier.of("secondary"),
                    )
                ) { primary, secondary ->
                    primary as FunctionValue
                    secondary as FunctionValue

                    object : FunctionValue() {
                        override fun apply(argument: Value): Thunk<Value> =
                            primary.apply(argument = argument).thenDo { result ->
                                when (result) {
                                    is UndefinedValue -> secondary.apply(
                                        argument = argument,
                                    )

                                    else -> Thunk.pure(result)
                                }
                            }

                        override fun dump(): String = "${primary.dump()} .. ${secondary.dump()}"
                    }
                }
            }

            override fun dump(): String = ".."
        }
    }
}

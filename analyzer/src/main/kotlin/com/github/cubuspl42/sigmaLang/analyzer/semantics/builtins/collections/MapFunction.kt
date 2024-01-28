package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.collections

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinMethodExtractor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

object MapFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = GenericType.orderedTraitDeclaration(
        Identifier.of("e"),
        Identifier.of("r"),
    )

    private val eTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 0L)),
    )

    private val rTypeVariable = TypeVariable(
        parameterDeclaration,
        path = TypeVariable.Path.of(IntValue(value = 1L)),
    )

    override val body = object : BuiltinMethodExtractor() {
        private val transformType = UniversalFunctionType(
            argumentType = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = eTypeVariable,
                    ),
                )
            ),
            imageType = rTypeVariable,
        )

        override val selfType: SpecificType = ArrayType(
            elementType = eTypeVariable,
        )

        override val methodArgumentType: TableType = OrderedTupleType(
            elements = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("transform"),
                    type = transformType,
                ),
            ),
        )

        override val methodImageType: SpecificType = ArrayType(
            elementType = rTypeVariable,
        )

        override fun computeMethodThunk(self: Value): Thunk<FunctionValue> {
            val elements = (self as FunctionValue).toList()

            return Thunk.pure(
                object : ComputableFunctionValue() {
                    override fun apply(argument: Value): Thunk<Value> {
                        val args = (argument as FunctionValue).toList()

                        val transform = args.first() as FunctionValue

                        return Thunk.traverseList(elements) {
                            transform.applyOrdered(it)
                        }.thenJust { values ->
                            DictValue.fromList(values)
                        }
                    }
                },
            )
        }
    }
}

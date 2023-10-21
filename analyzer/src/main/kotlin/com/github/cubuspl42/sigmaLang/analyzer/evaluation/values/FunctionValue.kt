package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.cutOffFront
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType

abstract class FunctionValue : Value() {

    object Link : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Thunk<Value> {
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

                                else -> result.toThunk()
                            }
                        }

                    override fun dump(): String = "${primary.dump()} .. ${secondary.dump()}"
                }
            }
        }

        override fun dump(): String = "(link function)"
    }

    object Chunked4 : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes: List<SpecificType> = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType: SpecificType = ArrayType(
            elementType = ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.chunked(size = 4)

            return DictValue.fromList(
                result.map { DictValue.fromList(it) },
            )
        }
    }

    object DropFirst : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes: List<SpecificType> = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType: SpecificType = ArrayType(
            elementType = elementTypeDefinition.typePlaceholder,
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.drop(1)

            return DictValue.fromList(result)
        }
    }

    object Take : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes: List<SpecificType> = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType: SpecificType = ArrayType(
            elementType = elementTypeDefinition.typePlaceholder,
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()
            val n = (args[1] as IntValue).value

            val result = elements.take(n.toInt())

            return DictValue.fromList(result)
        }
    }

    object Windows : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes: List<SpecificType> = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType: SpecificType = ArrayType(
            elementType = ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()
            val n = (args[1] as IntValue).value

            fun computeRecursive(
                elementsTail: List<Value>,
            ): List<List<Value>> {
                val (window) = elementsTail.cutOffFront(n.toInt()) ?: return emptyList()
                return listOf(window) + computeRecursive(elementsTail.drop(1))
            }

            val result = computeRecursive(elements)

            return DictValue.fromList(
                result.map {
                    DictValue.fromList(it)
                },
            )
        }
    }

    object MapFn : BuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        private val resultTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        private val transformType = UniversalFunctionType(
            argumentType = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = elementTypeDefinition.typePlaceholder,
                    ),
                )
            ),
            imageType = resultTypeDefinition.typePlaceholder,
        )

        override val argTypes = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
            transformType,
        )

        override val imageType = ArrayType(
            elementType = resultTypeDefinition.typePlaceholder,
        )

        override fun computeThunk(args: List<Value>): Thunk<Value> {
            val elements = (args[0] as FunctionValue).toList()
            val transform = args[1] as FunctionValue

            return Thunk.traverseList(elements) {
                transform.applyOrdered(it)
            }.thenJust { values ->
                DictValue.fromList(values)
            }
        }
    }

    object LengthFunction : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val list = (args[0] as FunctionValue).toList()

            return IntValue(value = list.size.toLong())
        }
    }

    object ConcatFunction : StrictBuiltinOrderedFunction() {
        private val elementTypeDefinition = TypeVariableDefinition(
            name = Identifier.of("elementType"),
        )

        override val argTypes = listOf(
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
            ArrayType(
                elementType = elementTypeDefinition.typePlaceholder,
            ),
        )

        override val imageType = ArrayType(
            elementType = elementTypeDefinition.typePlaceholder,
        )

        override fun compute(args: List<Value>): Value {
            val front = (args[0] as FunctionValue).toList()
            val back = (args[1] as FunctionValue).toList()

            return ArrayTable(front + back)
        }
    }

    object Sum : StrictBuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(elementType = IntCollectiveType),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()

            return IntValue(
                value = elements.sumOf { (it as IntValue).value },
            )
        }
    }

    object Product : StrictBuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(elementType = IntCollectiveType),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()

            return IntValue(
                value = elements.fold(
                    initial = 1,
                ) { acc, it ->
                    acc * (it as IntValue).value
                },
            )
        }
    }

    object Max : StrictBuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(elementType = IntCollectiveType),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()

            val result = elements.maxOf { (it as IntValue).value }

            return IntValue(value = result)
        }
    }

    fun toList(): List<Value> = generateSequence(0) { it + 1 }.map {
        apply(
            argument = IntValue(value = it.toLong()),
        ).evaluateInitialValue()
    }.takeWhile { it !is UndefinedValue }.toList()

    abstract fun apply(
        argument: Value,
    ): Thunk<Value>

    fun applyOrdered(
        vararg arguments: Value,
    ): Thunk<Value> = apply(
        argument = DictValue.fromList(arguments.toList()),
    )
}

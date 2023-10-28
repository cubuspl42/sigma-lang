package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.cutOffFront
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

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

    object Chunked4 : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body: BuiltinFunctionConstructor = object : StrictBuiltinOrderedFunctionConstructor() {

            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
            )

            override val imageType: SpecificType = ArrayType(
                elementType = ArrayType(
                    elementType = eTypeVariable,
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
    }

    object DropFirst : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body: BuiltinFunctionConstructor = object : StrictBuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("n"),
                    type = IntCollectiveType,
                ),
            )

            override val imageType = ArrayType(
                elementType = eTypeVariable,
            )

            override fun compute(args: List<Value>): Value {
                val elements = (args.first() as FunctionValue).toList()

                val result = elements.drop(1)

                return DictValue.fromList(result)
            }
        }
    }

    object Take : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body: BuiltinFunctionConstructor = object : StrictBuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("n"),
                    type = IntCollectiveType,
                ),
            )

            override val imageType = ArrayType(
                elementType = eTypeVariable,
            )

            override fun compute(args: List<Value>): Value {
                val elements = (args.first() as FunctionValue).toList()
                val n = (args[1] as IntValue).value

                val result = elements.take(n.toInt())

                return DictValue.fromList(result)
            }
        }
    }

    object Windows : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body: BuiltinFunctionConstructor = object : StrictBuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("n"),
                    type = IntCollectiveType,
                ),
            )

            override val imageType = ArrayType(
                elementType = ArrayType(
                    elementType = eTypeVariable,
                ),
            )

            override fun compute(args: List<Value>): Value {
                val elements = (args.first() as FunctionValue).toList()
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
    }

    object MapFn : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
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

        override val body = object : BuiltinOrderedFunctionConstructor() {
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

            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("transform"),
                    type = transformType,
                ),
            )

            override val imageType = ArrayType(
                elementType = rTypeVariable,
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
    }

    object LengthFunction : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body = object : StrictBuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("elements"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
            )

            override val imageType = IntCollectiveType

            override fun compute(args: List<Value>): Value {
                val list = (args[0] as FunctionValue).toList()

                return IntValue(value = list.size.toLong())
            }
        }
    }

    object ConcatFunction : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        override val body = object : StrictBuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("front"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("back"),
                    type = ArrayType(
                        elementType = eTypeVariable,
                    ),
                ),
            )

            override val imageType = ArrayType(
                elementType = eTypeVariable,
            )

            override fun compute(args: List<Value>): Value {
                val front = (args[0] as FunctionValue).toList()
                val back = (args[1] as FunctionValue).toList()

                return DictValue.fromList(front + back)
            }
        }
    }

    object Sum: StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("elements"),
                type = ArrayType(
                    elementType = IntCollectiveType,
                ),
            ),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()

            return IntValue(
                value = elements.sumOf { (it as IntValue).value },
            )
        }
    }

    object Product : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("elements"),
                type = ArrayType(
                    elementType = IntCollectiveType,
                ),
            ),
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

    object Max : StrictBuiltinOrderedFunctionConstructor() {
        override val argumentElements: List<OrderedTupleType.Element> = listOf(
            OrderedTupleType.Element(
                name = Identifier.of("elements"),
                type = ArrayType(
                    elementType = IntCollectiveType,
                ),
            ),
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

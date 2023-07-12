package sigma.evaluation.values

import cutOffFront
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.ArrayType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.evaluation.values.tables.ArrayTable
import sigma.evaluation.values.tables.DictTable

abstract class FunctionValue : Value() {
    object Link : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Value {
            argument as FunctionValue

            val primary = argument.apply(
                argument = Symbol.of("primary"),
            ) as FunctionValue

            val secondary = argument.apply(
                argument = Symbol.of("secondary"),
            ) as FunctionValue

            return object : FunctionValue() {
                override fun apply(argument: Value): Value {
                    val value = primary.apply(argument = argument)

                    return when (value) {
                        is UndefinedValue -> secondary.apply(argument = argument)
                        else -> value
                    }
                }

                override fun dump(): String = "${primary.dump()} .. ${secondary.dump()}"
            }
        }

        override fun dump(): String = "(link function)"
    }

    object Chunked4 : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.chunked(size = 4)

            return DictTable.fromList(
                result.map { DictTable.fromList(it) },
            )
        }
    }

    object DropFirst : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = TypeVariable(
                name = Symbol.of("e"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.drop(1)

            return DictTable.fromList(result)
        }
    }

    object Take : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = TypeVariable(
                name = Symbol.of("e"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()
            val n = (args[1] as IntValue).value

            val result = elements.take(n.toInt())

            return DictTable.fromList(result)
        }
    }

    object Windows : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
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

            return DictTable.fromList(
                result.map {
                    DictTable.fromList(it)
                },
            )
        }
    }

    object MapFn : BuiltinOrderedFunction() {
        private val transformType = UniversalFunctionType(
            argumentType = OrderedTupleType(
                elements = listOf(
                    OrderedTupleType.Element(
                        name = null,
                        type = TypeVariable(
                            name = Symbol.of("e"),
                        ),
                    ),
                )
            ),
            // TODO: Improve this typing, as it makes no sense
            imageType = TypeVariable(
                name = Symbol.of("r"),
            ),
        )

        override val argTypes = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
            transformType,
        )

        override val imageType = ArrayType(
            elementType = TypeVariable(
                name = Symbol.of("r"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()
            val transform = args[1] as FunctionValue

            return DictTable.fromList(elements.map {
                transform.apply(
                    DictTable.fromList(listOf(it)),
                )
            })
        }
    }

    object LengthFunction : BuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType = IntCollectiveType

        override fun compute(args: List<Value>): Value {
            val list = (args[0] as FunctionValue).toList()

            return IntValue(value = list.size.toLong())
        }
    }

    object ConcatFunction : BuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
            ArrayType(
                elementType = TypeVariable(
                    name = Symbol.of("e"),
                ),
            ),
        )

        override val imageType = ArrayType(
            elementType = TypeVariable(
                name = Symbol.of("e"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val front = (args[0] as FunctionValue).toList()
            val back = (args[1] as FunctionValue).toList()

            return ArrayTable(front + back)
        }
    }

    object Sum : BuiltinOrderedFunction() {
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

    object Product : BuiltinOrderedFunction() {
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

    object Max : BuiltinOrderedFunction() {
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
        apply(IntValue(value = it.toLong()))
    }.takeWhile { it !is UndefinedValue }.toList()

    abstract fun apply(
        argument: Value,
    ): Value
}

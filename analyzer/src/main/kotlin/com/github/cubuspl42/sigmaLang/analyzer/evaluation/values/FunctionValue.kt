package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.cutOffFront
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

abstract class FunctionValue : SealedValue() {

    object Link : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Thunk<Value> {
            argument as FunctionValue

            return Thunk.combine2(
                argument.apply(
                    argument = Symbol.of("primary"),
                ), argument.apply(
                    argument = Symbol.of("secondary"),
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

                                else -> result.asThunk
                            }
                        }

                    override fun dump(): String = "${primary.dump()} .. ${secondary.dump()}"
                }
            }
        }

        override fun dump(): String = "(link function)"
    }

    object Chunked4 : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
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

    object DropFirst : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = TypeVariable(
                formula = Formula.of("e"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args.first() as FunctionValue).toList()

            val result = elements.drop(1)

            return DictValue.fromList(result)
        }
    }

    object Take : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = TypeVariable(
                formula = Formula.of("e"),
            ),
        )

        override fun compute(args: List<Value>): Value {
            val elements = (args[0] as FunctionValue).toList()
            val n = (args[1] as IntValue).value

            val result = elements.take(n.toInt())

            return DictValue.fromList(result)
        }
    }

    object Windows : BuiltinOrderedFunction() {
        override val argTypes: List<Type> = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
        )

        override val imageType: Type = ArrayType(
            elementType = ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
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

            return DictValue.fromList(
                result.map {
                    DictValue.fromList(it)
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
                            formula = Formula.of("e"),
                        ),
                    ),
                )
            ),
            // TODO: Improve this typing, as it makes no sense
            imageType = TypeVariable(
                formula = Formula.of("r"),
            ),
        )

        override val argTypes = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
            transformType,
        )

        override val imageType = ArrayType(
            elementType = TypeVariable(
                formula = Formula.of("r"),
            ),
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

    object LengthFunction : BuiltinOrderedFunction() {
        override val argTypes = listOf(
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
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
                    formula = Formula.of("e"),
                ),
            ),
            ArrayType(
                elementType = TypeVariable(
                    formula = Formula.of("e"),
                ),
            ),
        )

        override val imageType = ArrayType(
            elementType = TypeVariable(
                formula = Formula.of("e"),
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

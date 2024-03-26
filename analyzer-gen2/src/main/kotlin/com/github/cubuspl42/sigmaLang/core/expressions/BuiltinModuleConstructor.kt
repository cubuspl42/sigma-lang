package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.values.builtin.BuiltinModule
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.typeNameOf

data object BuiltinModuleConstructor : Expression() {
    val builtinModuleTypeName = typeNameOf<BuiltinModule>()

    override val subExpressions: Set<Expression> = emptySet()

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : Expression.CodegenRepresentation() {
        override fun generateCode(): CodeBlock = CodeBlock.of("%T", builtinModuleTypeName)
    }

    override fun bind(
        scope: DynamicScope,
    ): Lazy<Value> = lazyOf(BuiltinModule)
}

class BuiltinModuleReference(
    val rawModuleReference: Expression,
) {
    class IfFunction(
        private val rawFunctionReference: Expression,
    ) : ShadowExpression() {
        fun call(
            condition: Expression,
            thenCase: Expression,
            elseCase: Expression,
        ) = rawFunctionReference.call(
            passedArgument = UnorderedTupleConstructor(
                valueByKey = mapOf(
                    Identifier(name = "condition") to lazyOf(condition),
                    Identifier(name = "then") to lazyOf(thenCase),
                    Identifier(name = "else") to lazyOf(elseCase),
                ),
            )
        )

        override val rawExpression: Expression
            get() = rawFunctionReference
    }


    val ifFunction = IfFunction(
        rawFunctionReference = rawModuleReference.readField(
            fieldName = Identifier.of("if"),
        )
    )

    class IsAFunction(
        private val rawFunctionReference: Expression,
    ) : ShadowExpression() {
        fun call(
            instance: Expression,
            class_: Expression,
        ) = rawFunctionReference.call(
            passedArgument = UnorderedTupleConstructor(
                valueByKey = mapOf(
                    Identifier(name = "instance") to lazyOf(instance),
                    Identifier(name = "class") to lazyOf(class_),
                ),
            )
        )

        override val rawExpression: Expression
            get() = rawFunctionReference
    }

    val isAFunction = IsAFunction(
        rawFunctionReference = rawModuleReference.readField(
            fieldName = Identifier.of("isA"),
        )
    )

    class PanicFunction(
        private val rawFunctionReference: Expression,
    ) : ShadowExpression() {
        fun call() = rawFunctionReference.call(
            passedArgument = UnorderedTupleConstructor.Empty
        )

        override val rawExpression: Expression
            get() = rawFunctionReference
    }

    val panicFunction = PanicFunction(
        rawFunctionReference = rawModuleReference.readField(
            fieldName = Identifier.of("panic"),
        )
    )

    class ListClassReference(
        private val rawClassReference: Expression,
    ) {
        abstract class NoArgMethod(
            private val rawMethodReference: Expression,
        ) {
            fun call(
                list: Expression,
            ): Expression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("this"),
                        value = lazyOf(list),
                    )
                )
            )
        }

        inner class HeadMethod : NoArgMethod(
            rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("head"),
            )
        )

        val head = HeadMethod()

        inner class TailMethod : NoArgMethod(
            rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("tail"),
            )
        )

        val tail = TailMethod()

        inner class IsEmptyMethod : NoArgMethod(
            rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("isEmpty"),
            )
        )

        val isEmpty = IsEmptyMethod()

        inner class IsNotEmptyMethod : NoArgMethod(
            rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("isNotEmpty"),
            )
        )

        val isNotEmpty = IsNotEmptyMethod()

        inner class ConcatMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("concat"),
            )

            fun call(
                list: Expression,
                otherList: Expression,
            ): Expression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("left"),
                        value = lazyOf(list),
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("right"),
                        value = lazyOf(otherList),
                    ),
                ),
            )
        }

        val concat = ConcatMethod()
    }

    val listClass = ListClassReference(
        rawClassReference = rawModuleReference.readField(
            fieldName = Identifier.of("List"),
        ),
    )

    class DictClassReference(
        private val rawClassReference: Expression,
    ) {
        abstract class NoArgMethod(
            private val rawMethodReference: Expression,
        ) {
            fun call(
                dict: Expression,
            ): Expression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("this"),
                        value = lazyOf(dict),
                    )
                )
            )
        }

        inner class UnionWithMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("unionWith"),
            )

            fun call(
                dict: Expression,
                otherDict: Expression,
            ): Expression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("first"),
                        value = lazy { dict },
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("second"),
                        value = lazy { otherDict },
                    ),
                ),
            )
        }

        val unionWith = UnionWithMethod()
    }

    val dictClass = DictClassReference(
        rawClassReference = rawModuleReference.readField(
            fieldName = Identifier.of("Dict"),
        ),
    )

    class StringClassReference(
        private val rawClassReference: Expression,
    ) {
        inner class ConcatMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("concat"),
            )

            fun call(
                string: Expression,
                otherString: Expression,
            ): Expression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("left"),
                        value = lazyOf(string),
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("right"),
                        value = lazyOf(otherString),
                    ),
                ),
            )
        }

        val concat = ConcatMethod()
    }

    val stringClass = StringClassReference(
        rawClassReference = rawModuleReference.readField(
            fieldName = Identifier.of("String"),
        ),
    )

    val classModule = ClassModuleExpression(
        rawModuleExpression = rawModuleReference.readField(
            fieldName = Identifier.of("Class"),
        ),
    )
}

class ClassModuleExpression(
    private val rawModuleExpression: Expression,
) {
    inner class Of : ShadowExpression() {
        private val rawMethodExpression: Expression = rawModuleExpression.readField(
            fieldName = Identifier.of("of"),
        )

        override val rawExpression: Expression
            get() = rawModuleExpression

        fun call(
            tag: Identifier,
            instanceConstructorName: Identifier,
            methodByName: Map<Identifier, AbstractionConstructor>,
        ): Expression = rawMethodExpression.call(
            passedArgument = UnorderedTupleConstructor.fromEntries(
                UnorderedTupleConstructor.Entry(
                    key = Identifier.of("tag"),
                    value = lazyOf(tag.toLiteral()),
                ),
                UnorderedTupleConstructor.Entry(
                    key = Identifier.of("instanceConstructorName"),
                    value = lazyOf(instanceConstructorName.toLiteral()),
                ),
                UnorderedTupleConstructor.Entry(
                    key = Identifier.of("methods"),
                    value = lazyOf(
                        UnorderedTupleConstructor.fromEntries(
                            methodByName.map { (name, implementation) ->
                                UnorderedTupleConstructor.Entry(
                                    key = name, value = lazyOf(implementation)
                                )
                            },
                        )
                    ),
                ),
            ),
        )
    }

    val of = Of()
}

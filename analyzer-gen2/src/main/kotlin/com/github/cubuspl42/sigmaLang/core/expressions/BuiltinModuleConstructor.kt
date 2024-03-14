package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.call
import com.github.cubuspl42.sigmaLang.core.readField
import com.github.cubuspl42.sigmaLang.core.values.builtin.BuiltinModule
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
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
    val rawModuleReference: ShadowExpression,
) {
    class IfFunction(
        private val rawFunctionReference: Expression,
    ) : ShadowExpression() {
        fun call(
            condition: ShadowExpression,
            thenCase: ShadowExpression,
            elseCase: ShadowExpression,
        ) = rawFunctionReference.call(
            passedArgument = UnorderedTupleConstructor(
                valueByKey = mapOf(
                    Identifier(name = "condition") to lazyOf(condition.rawExpression),
                    Identifier(name = "then") to lazyOf(thenCase.rawExpression),
                    Identifier(name = "else") to lazyOf(elseCase.rawExpression),
                ),
            )
        )

        override val rawExpression: Expression
            get() = rawFunctionReference
    }


    val ifFunction = IfFunction(
        rawFunctionReference = rawModuleReference.readField(
            fieldName = Identifier.of("if"),
        ).rawExpression
    )

    class IsAFunction(
        private val rawFunctionReference: Expression,
    ) : ShadowExpression() {
        fun call(
            instance: ShadowExpression,
            class_: ShadowExpression,
        ) = rawFunctionReference.call(
            passedArgument = UnorderedTupleConstructor(
                valueByKey = mapOf(
                    Identifier(name = "instance") to lazyOf(instance.rawExpression),
                    Identifier(name = "class") to lazyOf(class_.rawExpression),
                ),
            )
        )

        override val rawExpression: Expression
            get() = rawFunctionReference
    }

    val isAFunction = IsAFunction(
        rawFunctionReference = rawModuleReference.readField(
            fieldName = Identifier.of("isA"),
        ).rawExpression
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
        ).rawExpression
    )

    class ListClassReference(
        private val rawClassReference: ShadowExpression,
    ) {
        abstract class NoArgMethod(
            private val rawMethodReference: ShadowExpression,
        ) {
            fun call(
                list: ShadowExpression,
            ): ShadowExpression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("this"),
                        value = lazyOf(list.rawExpression),
                    )
                )
            )
        }

        inner class HeadMethod : NoArgMethod(
            rawMethodReference = rawClassReference.rawExpression.readField(
                fieldName = Identifier.of("head"),
            )
        )

        val head = HeadMethod()

        inner class TailMethod : NoArgMethod(
            rawMethodReference = rawClassReference.rawExpression.readField(
                fieldName = Identifier.of("tail"),
            )
        )

        val tail = TailMethod()

        inner class IsNotEmptyMethod : NoArgMethod(
            rawMethodReference = rawClassReference.rawExpression.readField(
                fieldName = Identifier.of("isNotEmpty"),
            )
        )

        val isNotEmpty = IsNotEmptyMethod()

        inner class ConcatMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("concat"),
            )

            fun call(
                list: ShadowExpression,
                otherList: ShadowExpression,
            ): ShadowExpression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("left"),
                        value = lazyOf(list.rawExpression),
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("right"),
                        value = lazyOf(otherList.rawExpression),
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
        private val rawClassReference: ShadowExpression,
    ) {
        abstract class NoArgMethod(
            private val rawMethodReference: ShadowExpression,
        ) {
            fun call(
                dict: ShadowExpression,
            ): ShadowExpression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("this"),
                        value = lazyOf(dict.rawExpression),
                    )
                )
            )
        }

        inner class UnionWithMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("unionWith"),
            )

            fun call(
                dict: ShadowExpression,
                otherDict: ShadowExpression,
            ): ShadowExpression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("first"),
                        value = lazy { dict.rawExpression },
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("second"),
                        value = lazy { otherDict.rawExpression },
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
        private val rawClassReference: ShadowExpression,
    ) {
        inner class ConcatMethod {
            private val rawMethodReference = rawClassReference.readField(
                fieldName = Identifier.of("concat"),
            )

            fun call(
                string: ShadowExpression,
                otherString: ShadowExpression,
            ): ShadowExpression = rawMethodReference.call(
                passedArgument = UnorderedTupleConstructor.fromEntries(
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("left"),
                        value = lazyOf(string.rawExpression),
                    ),
                    UnorderedTupleConstructor.Entry(
                        key = Identifier.of("right"),
                        value = lazyOf(otherString.rawExpression),
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
    private val rawModuleExpression: ShadowExpression,
) {
    inner class Of : ShadowExpression() {
        private val rawMethodExpression: ShadowExpression = rawModuleExpression.readField(
            fieldName = Identifier.of("of"),
        )

        override val rawExpression: Expression
            get() = rawModuleExpression.rawExpression

        fun call(
            tag: Identifier,
            instanceConstructorName: Identifier,
            methodByName: Map<Identifier, AbstractionConstructor>,
        ): ShadowExpression = rawMethodExpression.call(
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

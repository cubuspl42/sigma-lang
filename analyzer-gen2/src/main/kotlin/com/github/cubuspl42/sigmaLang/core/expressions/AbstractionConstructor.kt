package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.values.AbstractionValue
import com.github.cubuspl42.sigmaLang.core.values.ExpressedAbstractionValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

fun <T> unionAll(sets: Iterable<Set<T>>) = sets.fold(emptySet<T>()) { acc, set -> acc + set }

class AbstractionConstructor(
    val body: Expression,
) : Wrapper() {
    abstract class CodegenRepresentation : Expression.CodegenRepresentation() {
        abstract val argumentName: String

        abstract val body: Expression.CodegenRepresentation

        final override fun generateCode(): CodeBlock {
            val abstractionObjectBuilder = TypeSpec.anonymousClassBuilder().superclass(typeNameOf<AbstractionValue>())
                .addSuperclassConstructorParameter("").addFunction(
                    FunSpec.builder(name = "compute").addParameter(
                        name = "argument",
                        type = Value::class,
                    ).addModifiers(KModifier.OVERRIDE).returns(
                            returnType = Value::class,
                        ).addCode(
                            """
                                    val $argumentName = argument
                                    return %L
                                """.trimIndent(),
                            body.generateCode(),
                        ).build()
                )

            return CodeBlock.of(
                "%L",
                abstractionObjectBuilder.build(),
            )
        }
    }

    companion object {
        fun looped1(
            buildBody: (ArgumentReference) -> Expression,
        ): AbstractionConstructor = looped {
            Pair(
                buildBody(it),
                Unit,
            )
        }.first

        fun <T> looped(
            buildBody: (ArgumentReference) -> Pair<Expression, T>,
        ): Pair<AbstractionConstructor, T> = LazyUtils.looped2 { abstractionConstructorLooped, _ ->
            val reference = ArgumentReference(
                referredAbstractionLazy = abstractionConstructorLooped,
            )

            val (body, t) = buildBody(reference)

            val abstractionConstructor = AbstractionConstructor(
                body = body,
            )

            Pair(
                abstractionConstructor,
                t,
            )
        }

        fun builder(
            buildImageBuilder: (ArgumentReference) -> ExpressionBuilder<Expression>,
        ): ExpressionBuilder<AbstractionConstructor> = object : ExpressionBuilder<AbstractionConstructor>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): AbstractionConstructor = AbstractionConstructor.looped1 { argumentReference ->
                val imageBuilder = buildImageBuilder(argumentReference)

                imageBuilder.build(
                    buildContext = buildContext,
                )
            }
        }
    }

    override val subExpressions: Set<Expression> = setOf(body)

    /**
     * Expressions "wrapped" by this abstraction. Being wrapped means that this abstraction is the closest outer
     * abstraction for that expression. References can't be wrapped.
     */
    val wrappedExpressions: Set<ComplexExpression> by lazy {
        collectWrappedExpressions(body)
    }

    private fun collectWrappedExpressions(
        expression: Expression,
    ): Set<ComplexExpression> = if (expression is ComplexExpression) {
        setOf(expression) + when (expression) {
            is Wrapper -> emptySet()
            is Call -> collectWrappedExpressions(expression.callee) + collectWrappedExpressions(expression.passedArgument)
            is UnorderedTupleConstructor -> unionAll(expression.values.map { collectWrappedExpressions(it.value) })
            is OrderedTupleConstructor -> TODO()
        }
    } else {
        emptySet()
    }

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): Expression.CodegenRepresentation = object : CodegenRepresentation() {
        override val argumentName: String = context.generateUniqueName(prefix = "arg")

        override val body: Expression.CodegenRepresentation by lazy {
            context.getRepresentation(this@AbstractionConstructor.body)
        }
    }

    override fun bind(context: DynamicContext): Lazy<Value> = lazyOf(
        ExpressedAbstractionValue(
            abstractionConstructor = this,
            closure = context,
        )
    )
}

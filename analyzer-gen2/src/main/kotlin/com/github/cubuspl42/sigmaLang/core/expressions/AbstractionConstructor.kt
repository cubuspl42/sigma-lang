package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.ExpressedAbstraction
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

fun <T> unionAll(sets: Iterable<Set<T>>) = sets.fold(emptySet<T>()) { acc, set -> acc + set }

class AbstractionConstructor(
    val body: Expression,
) : ComplexExpression() {
    abstract class CodegenRepresentation : Expression.InnerCodegenRepresentation() {
        abstract val argumentName: String

        abstract val localProperties: List<Expression.NamedCodegenRepresentation>

        abstract val body: Expression.OuterCodegenRepresentation

        final override fun generateCode(): CodeBlock {
            val abstractionObjectBuilder =
                TypeSpec.anonymousClassBuilder()
                    .superclass(typeNameOf<Abstraction>())
                    .addSuperclassConstructorParameter("")
                    .addFunction(
                        FunSpec.builder(name = "compute").addParameter(
                            name = "argument",
                            type = Value::class,
                        ).addModifiers(KModifier.OVERRIDE)
                            .returns(
                                returnType = Value::class,
                            )
                            .addCode(generateBodyCode())
                            .build()
                    )

            return CodeBlock.of("lazyOf(%L)", abstractionObjectBuilder.build())
        }

        private fun generateBodyCode(): CodeBlock {
            val helperObjectBuilder = TypeSpec.anonymousClassBuilder()
                .addProperty(
                    PropertySpec.builder(
                        name = argumentName,
                        type = Module.CodegenRepresentationContext.lazyValueTypeName,
                    )
                        .initializer("lazyOf(argument)")
                        .build()
                )

            localProperties.forEach {
                helperObjectBuilder.addProperty(it.generateDefinition())
            }

            helperObjectBuilder.addProperty(
                PropertySpec.builder(
                    name = "result",
                    type = Module.CodegenRepresentationContext.lazyValueTypeName,
                )
                    .initializer(body.generateUsage())
                    .build()
            )

            return CodeBlock.of("return %L.result.value", helperObjectBuilder.build())
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
            is AbstractionConstructor -> emptySet()
            is Call -> collectWrappedExpressions(expression.callee) + collectWrappedExpressions(expression.passedArgument)
            is UnorderedTupleConstructor -> unionAll(expression.values.map { collectWrappedExpressions(it.value) })
        }
    } else {
        emptySet()
    }

    override fun buildInnerCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): InnerCodegenRepresentation = object : CodegenRepresentation() {
        override val argumentName: String = context.generateUniqueName()

        override val localProperties: List<NamedCodegenRepresentation> by lazy {
            wrappedExpressions.mapNotNull {
                context.getRepresentation(it) as? NamedCodegenRepresentation
            }
        }

        override val body: OuterCodegenRepresentation by lazy {
            context.getRepresentation(this@AbstractionConstructor.body)
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        ExpressedAbstraction(
            abstractionConstructor = this,
            closure = scope,
        )
    )
}

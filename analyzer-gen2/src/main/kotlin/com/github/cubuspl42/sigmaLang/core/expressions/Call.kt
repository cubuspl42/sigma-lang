package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class Call(
    val calleeLazy: Lazy<Expression>,
    val passedArgumentLazy: Lazy<Expression>,
) : ComplexExpression() {

    val callee by calleeLazy
    val passedArgument by passedArgumentLazy

    override val subExpressions: Set<Expression> by lazy {
        setOf(callee, passedArgument)
    }

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            return CodeBlock.of(
                """
                    lazyOf(
                    ⇥(%L.value as %T).call(
                    ⇥argument = %L.value,
                    ⇤)
                    ⇤)
                """.trimIndent(),
                context.getRepresentation(callee).generateCode(),
                Callable::class,
                context.getRepresentation(passedArgument).generateCode(),
            )
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValue = callee.bind(scope = scope).value as Callable
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

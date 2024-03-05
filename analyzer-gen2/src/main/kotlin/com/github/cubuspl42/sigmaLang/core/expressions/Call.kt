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
    companion object {
        fun generateCallCode(
            callee: CodeBlock,
            passedArgument: CodeBlock,
        ): CodeBlock = CodeBlock.of(
            """
                (%L as %T).call(
                ⇥argument = %L,
                ⇤)
            """.trimIndent(),
            callee,
            Callable::class,
            passedArgument
        )
    }

    val callee by calleeLazy
    val passedArgument by passedArgumentLazy

    override val subExpressions: Set<Expression> by lazy {
        setOf(callee, passedArgument)
    }

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = generateCallCode(
            callee = context.getRepresentation(callee).generateCode(),
            passedArgument = context.getRepresentation(passedArgument).generateCode(),
        )
    }

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValue = callee.bind(scope = scope).value as Callable
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

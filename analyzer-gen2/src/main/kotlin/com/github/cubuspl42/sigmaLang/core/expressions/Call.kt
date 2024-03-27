package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.values.CallableValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class Call(
    val callee: Expression,
    val passedArgument: Expression,
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
            CallableValue::class,
            passedArgument
        )

        fun builder(
            calleeBuilder: ExpressionBuilder<Expression>,
            passedArgumentBuilder: ExpressionBuilder<Expression>,
        ): ExpressionBuilder<Call> = object : ExpressionBuilder<Call>() {
            override fun build(buildContext: BuildContext): Call = Call(
                callee = calleeBuilder.build(buildContext),
                passedArgument = passedArgumentBuilder.build(buildContext),
            )
        }
    }

    override val subExpressions: Set<Expression> by lazy {
        setOf(callee, passedArgument)
    }

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = generateCallCode(
            callee = context.getRepresentation(callee).generateCode(),
            passedArgument = context.getRepresentation(passedArgument).generateCode(),
        )
    }

    override fun bind(context: DynamicContext): Lazy<Value> {
        val calleeValueLazy = callee.bind(context = context)
        val passedArgumentValueLazy = passedArgument.bind(context = context)

        return lazy {
            val calleeValue = calleeValueLazy.value as CallableValue
            val passedArgumentValue = passedArgumentValueLazy.value

            calleeValue.call(argument = passedArgumentValue)
        }
    }
}

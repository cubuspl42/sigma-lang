package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.buildRaw
import com.github.cubuspl42.sigmaLang.core.values.CallableValue
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class Call(
    val callee: Expression,
    val passedArgument: Expression,
) : ComplexExpression() {
    object FieldRead {
        fun builder(
            subjectBuilder: ExpressionBuilder<Expression>,
            fieldName: Identifier,
        ) = Call.builder(
            calleeBuilder = subjectBuilder,
            passedArgumentBuilder = ExpressionBuilder.pure(fieldName.toLiteral()),
        )
    }

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

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValueLazy = callee.bind(scope = scope)
        val passedArgumentValueLazy = passedArgument.bind(scope = scope)

        return lazy {
            val calleeValue = calleeValueLazy.value as CallableValue
            val passedArgumentValue = passedArgumentValueLazy.value

            calleeValue.call(argument = passedArgumentValue)
        }
    }
}

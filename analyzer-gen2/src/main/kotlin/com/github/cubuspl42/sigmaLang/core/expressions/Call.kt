package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class Call(
    val calleeLazy: Lazy<Expression>,
    val passedArgumentLazy: Lazy<Expression>,
) : ComplexExpression() {
    object FieldRead {
        fun builder(
            subjectBuilder: ExpressionBuilder<*>,
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
            Callable::class,
            passedArgument
        )

        fun builder(
            calleeBuilder: ExpressionBuilder<*>,
            passedArgumentBuilder: ExpressionBuilder<*>,
        ): ExpressionBuilder<Call> = object : ExpressionBuilder<Call>() {
            override fun build(buildContext: BuildContext): Call = Call(
                calleeLazy = lazyOf(calleeBuilder.buildRaw(buildContext)),
                passedArgumentLazy = lazyOf(passedArgumentBuilder.buildRaw(buildContext)),
            )
        }
    }

    val callee by calleeLazy
    val passedArgument by passedArgumentLazy

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
        val calleeValue = callee.bind(scope = scope).value as Callable
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

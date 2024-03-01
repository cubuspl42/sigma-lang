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
        fun fieldRead(
            subjectLazy: Lazy<Expression>,
            readFieldName: Identifier,
        ): Lazy<Expression> = lazyOf(
            Call(
                calleeLazy = subjectLazy,
                passedArgumentLazy = lazyOf(IdentifierLiteral(value = readFieldName)),
            ),
        )
    }

    val callee by calleeLazy
    val passedArgument by passedArgumentLazy

    override val subExpressions: Set<Expression> = setOf(callee, passedArgument)

    override fun buildInnerCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): InnerCodegenRepresentation = object : InnerCodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            return CodeBlock.of(
                """
                    lazyOf(
                    ⇥(%L.value as %T).call(
                    ⇥argument = %L.value,
                    ⇤)
                    ⇤)
                """.trimIndent(),
                context.getRepresentation(callee).generateUsage(),
                Callable::class,
                context.getRepresentation(passedArgument).generateUsage(),
            )
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> {
        val calleeValue = callee.bind(scope = scope).value as Callable
        val passedArgumentValue = passedArgument.bind(scope = scope).value

        return lazyOf(calleeValue.call(argument = passedArgumentValue))
    }
}

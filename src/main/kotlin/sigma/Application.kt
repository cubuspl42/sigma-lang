package sigma

import sigma.parser.antlr.SigmaParser.ApplicationAltContext
import kotlin.String

data class Application(
    val subject: Expression,
    val argument: Expression,
) : Expression {
    companion object {
        fun build(
            read: ApplicationAltContext,
        ): Application = Application(
            subject = Expression.build(read.subject),
            argument = Expression.build(read.key),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value {
        val subjectValue = subject.evaluate(scope = scope) as? FunctionValue
            ?: throw IllegalStateException("Subject is not a function")

        val argumentValue = argument.evaluate(scope = scope)

        return subjectValue.apply(
            argument = argumentValue,
        )
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}

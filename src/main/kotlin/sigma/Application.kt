package sigma

import sigma.parser.antlr.SigmaParser.ReadAltContext
import kotlin.String

data class Application(
    val subject: Expression,
    val key: Expression,
) : Expression {
    companion object {
        fun build(
            read: ReadAltContext,
        ): Application = Application(
            subject = Expression.build(read.subject),
            key = Expression.build(read.key),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value {
        val subjectValue = subject.evaluate(scope = scope)
        val keyValue = key.evaluate(scope = scope)

        return subjectValue.apply(
            scope = scope,
            key = keyValue,
        )
    }

    override fun dump(): String = "${subject.dump()}[${key.dump()}]"
}

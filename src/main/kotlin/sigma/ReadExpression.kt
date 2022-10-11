package sigma

import sigma.parser.antlr.SigmaParser.ReadAltContext

data class ReadExpression(
    val subject: Expression,
    val key: Expression,
) : Expression {
    companion object {
        fun build(
            read: ReadAltContext,
        ): ReadExpression = ReadExpression(
            subject = Expression.build(read.subject),
            key = Expression.build(read.key),
        )
    }

    override fun evaluate(): Value {
        val subjectValue = subject.evaluate() as ObjectValue
        val keyValue = key.evaluate()

        return subjectValue.getValue(keyValue) ?: throw IllegalStateException()
    }

    override fun dump(): String = "${subject.dump()}[${key.dump()}]"
}

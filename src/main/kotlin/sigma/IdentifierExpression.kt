package sigma

import sigma.parser.antlr.SigmaParser.IdentifierContext

data class IdentifierExpression(
    val value: String,
) : Expression {
    companion object {
        fun build(
            identifier: IdentifierContext,
        ): IdentifierExpression = IdentifierExpression(
            value = identifier.text,
        )
    }

    override fun dump(): String = value
}

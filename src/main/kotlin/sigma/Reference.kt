package sigma

import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import kotlin.String

data class Reference(
    val referee: String,
) : Expression {
    companion object {
        fun build(
            identifier: ReferenceAltContext,
        ): Reference = Reference(
            referee = identifier.referee.text,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = scope.get(referee)

    override fun dump(): String = referee
}

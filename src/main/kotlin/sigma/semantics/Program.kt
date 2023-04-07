package sigma.semantics

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.BuiltinTypeScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.syntax.ModuleTerm
import sigma.values.Value

class Program internal constructor(
    private val prelude: Prelude,
    private val root: ModuleTerm,
) {
    companion object {
        internal fun buildParser(
            sourceName: String,
            source: String,
        ): SigmaParser {
            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)

            return SigmaParser(tokenStream)
        }
    }

    fun validate() {
        root.validate(
            typeScope = BuiltinTypeScope,
            valueScope = prelude.valueScope,
        )
    }

    fun evaluateResult(): Value {
        root.validate(
            typeScope = BuiltinTypeScope,
            valueScope = prelude.valueScope,
        )

        val result = root.evaluateDeclaration(
            name = "main",
            scope = prelude.scope,
        )

        return result.toEvaluatedValue
    }
}

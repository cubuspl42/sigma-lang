package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.syntax.expressions.ExpressionTerm

abstract class Term {
    abstract val location: SourceLocation

    // TODO: Improve this! Merge with `inferType`?
    open fun validate(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) {
    }
}

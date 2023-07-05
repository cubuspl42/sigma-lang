package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.semantics.Program
import sigma.syntax.LocalDefinitionTerm
import sigma.syntax.SourceLocation
import sigma.syntax.Term

data class LocalScopeTerm(
    override val location: SourceLocation,
    val definitions: List<LocalDefinitionTerm>,
) : Term() {
    companion object {
        fun parse(
            sourceName: String,
            source: String,
        ): LocalScopeTerm = build(
            ctx = Program.buildParser(
                sourceName = sourceName,
                source = source,
            ).localScope(),
        )

        fun build(
            ctx: SigmaParser.LocalScopeContext,
        ): LocalScopeTerm = LocalScopeTerm(
            location = SourceLocation.build(ctx),
            definitions = ctx.declaration().map {
                LocalDefinitionTerm.build(it)
            },
        )
    }
}
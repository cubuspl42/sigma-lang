package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.semantics.Program
import sigma.syntax.LocalDefinitionSourceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.SourceTerm

data class LocalScopeSourceTerm(
    override val location: SourceLocation,
    val definitions: List<LocalDefinitionSourceTerm>,
) : SourceTerm() {
    companion object {
        fun parse(
            sourceName: String,
            source: String,
        ): LocalScopeSourceTerm = build(
            ctx = Program.buildParser(
                sourceName = sourceName,
                source = source,
            ).localScope(),
        )

        fun build(
            ctx: SigmaParser.LocalScopeContext,
        ): LocalScopeSourceTerm = LocalScopeSourceTerm(
            location = SourceLocation.build(ctx),
            definitions = ctx.definition().map {
                LocalDefinitionSourceTerm.build(it)
            },
        )
    }
}

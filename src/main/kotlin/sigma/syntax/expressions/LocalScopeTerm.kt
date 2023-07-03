package sigma.syntax.expressions

import sigma.evaluation.scope.LoopedScope
import sigma.evaluation.scope.Scope
import sigma.parser.antlr.SigmaParser
import sigma.semantics.Program
import sigma.syntax.DefinitionTerm
import sigma.syntax.SourceLocation
import sigma.syntax.Term

data class LocalScopeTerm(
    override val location: SourceLocation,
    val definitions: List<DefinitionTerm>,
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
                DefinitionTerm.build(it)
            },
        )
    }

    fun evaluateDynamically(
        scope: Scope,
    ): Scope = LoopedScope(
        context = scope,
        declarations = definitions.associate {
            it.name to it.value
        },
    )
}
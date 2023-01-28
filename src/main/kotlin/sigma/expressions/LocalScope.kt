package sigma.expressions

import sigma.compiler.Program
import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser
import sigma.values.LoopedStaticValueScope
import sigma.values.tables.LoopedScope
import sigma.values.tables.Scope

data class LocalScope(
    override val location: SourceLocation,
    val declarations: List<Declaration>,
) : Term() {
    companion object {
        fun parse(
            sourceName: String,
            source: String,
        ): LocalScope = build(
            ctx = Program.buildParser(
                sourceName = sourceName,
                source = source,
            ).localScope(),
        )

        fun build(
            ctx: SigmaParser.LocalScopeContext,
        ): LocalScope = LocalScope(
            location = SourceLocation.build(ctx),
            declarations = ctx.declaration().map {
                Declaration.build(it)
            },
        )
    }

    fun evaluateStatically(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): StaticValueScope = LoopedStaticValueScope(
        typeContext = typeScope,
        valueContext = valueScope,
        declarations = declarations,
    )

    fun evaluateDynamically(
        scope: Scope,
    ): Scope = LoopedScope(
        context = scope,
        declarations = declarations.associate {
            it.name to it.value
        },
    )
}
package sigma.syntax.expressions

import sigma.semantics.Program
import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser
import sigma.syntax.DefinitionTerm
import sigma.syntax.SourceLocation
import sigma.syntax.Term
import sigma.values.LoopedStaticValueScope
import sigma.values.tables.LoopedScope
import sigma.values.tables.Scope

data class LocalScopeTerm(
    override val location: SourceLocation,
    val declarations: List<DefinitionTerm>,
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
            declarations = ctx.declaration().map {
                DefinitionTerm.build(it)
            },
        )
    }

    override fun validate(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) {
        val newValueScope = evaluateStatically(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        declarations.forEach {
            it.validate(
                typeScope = typeScope,
                valueScope = newValueScope,
            )
        }
    }

    fun evaluateStatically(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): SyntaxValueScope = LoopedStaticValueScope(
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
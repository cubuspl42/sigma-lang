package sigma.syntax.expressions


import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.Type
import sigma.values.IntValue
import sigma.values.Value
import sigma.values.tables.Scope

data class IntLiteralTerm(
    override val location: SourceLocation,
    val value: IntValue,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteralTerm = IntLiteralTerm(
            location = SourceLocation.build(ctx),
            value = IntValue(value = ctx.text.toLong()),
        )
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = IntLiteralType(
        value = value,
    )

    override fun evaluate(
        scope: Scope,
    ): Value = value

    override fun dump(): String = value.toString()
}

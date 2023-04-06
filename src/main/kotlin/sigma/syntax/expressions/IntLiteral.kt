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

data class IntLiteral(
    override val location: SourceLocation,
    val value: IntValue,
) : Expression() {
    companion object {
        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteral = IntLiteral(
            location = SourceLocation.build(ctx),
            value = IntValue(value = ctx.text.toLong()),
        )
    }

    override fun inferType(
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

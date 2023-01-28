package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteral
import sigma.types.AbstractionType
import sigma.types.MetaType
import sigma.types.OrderedTupleType
import sigma.types.TableType
import sigma.types.Type
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.tables.Scope

data class Abstraction(
    override val location: SourceLocation,
    val metaArgument: MetaArgumentExpression? = null,
    val argumentType: TupleTypeLiteral,
    val image: Expression,
) : Expression() {
    data class MetaArgumentExpression(
        override val location: SourceLocation,
        val name: Symbol,
    ): Term() {
        companion object {
            fun build(
                ctx: MetaArgumentContext,
            ): MetaArgumentExpression =
                MetaArgumentExpression(
                    location = SourceLocation.build(ctx),
                    name = Symbol.of(ctx.name.text),
                )
        }
    }

    companion object {
        fun build(
            ctx: AbstractionContext,
        ): Abstraction = Abstraction(
            location = SourceLocation.build(ctx),
            metaArgument = ctx.metaArgument()?.let {
                MetaArgumentExpression.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeLiteral.build(it)
            },
            image = Expression.build(ctx.image),
        )
    }

    override fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val argumentType = argumentType.evaluate(
            typeScope = typeScope,
        )

        val innerScope = argumentType.toStaticValueScope().chainWith(
            valueScope,
        )

        return AbstractionType(
            argumentType = argumentType,
            imageType = image.inferType(typeScope = typeScope, valueScope = innerScope),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Closure = Closure(
        context = scope,
        argumentType = argumentType,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

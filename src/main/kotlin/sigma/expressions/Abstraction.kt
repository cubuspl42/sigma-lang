package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.typeExpressions.TypeExpression
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.typeExpressions.TupleTypeLiteral
import sigma.types.AbstractionType
import sigma.types.MetaType
import sigma.types.OrderedTupleType
import sigma.types.TableType
import sigma.types.Type
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.TypeError
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
    ) : TypeExpression() {
        companion object {
            fun build(
                ctx: MetaArgumentContext,
            ): MetaArgumentExpression = MetaArgumentExpression(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
            )
        }

        override fun evaluate(
            typeScope: StaticTypeScope,
        ): Type = OrderedTupleType(
            elements = listOf(
                OrderedTupleType.Element(
                    name = name,
                    type = MetaType,
                )
            ),
        )
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
        val metaArgumentType = metaArgument?.let {
            it.evaluate(
                typeScope = typeScope,
            ) as? TableType ?: throw TypeError(
                location = location,
                message = "Meta-arguments have to be of table type",
            )
        } ?: TableType.Empty

        val argumentType = argumentType.evaluate(
            typeScope = typeScope,
        )

        val innerScope = argumentType.toStaticValueScope().chainWith(
            valueScope,
        )

        return AbstractionType(
            metaArgumentType = metaArgumentType,
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

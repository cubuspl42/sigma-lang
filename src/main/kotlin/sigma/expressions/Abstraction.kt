package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.types.AbstractionType
import sigma.types.ArrayType
import sigma.types.MetaType
import sigma.types.TableType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.values.Closure
import sigma.values.FixedStaticValueScope
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.Value
import sigma.values.tables.FixedScope
import sigma.values.tables.Scope
import sigma.values.tables.Table

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
        ): Type = ArrayType(
            elementType = MetaType,
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

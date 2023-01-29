package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteral
import sigma.types.AbstractionType
import sigma.types.Type
import sigma.types.TypeVariable
import sigma.values.Closure
import sigma.values.FixedStaticTypeScope
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
    ) : Term() {
        companion object {
            fun build(
                ctx: MetaArgumentContext,
            ): MetaArgumentExpression = MetaArgumentExpression(
                location = SourceLocation.build(ctx),
                name = Symbol.of(ctx.name.text),
            )
        }

        fun toStaticTypeScope(): StaticTypeScope = FixedStaticTypeScope(
            entries = mapOf(
                name to TypeVariable,
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
        val innerTypeScope = metaArgument?.toStaticTypeScope()?.chainWith(
            backScope = typeScope,
        ) ?: typeScope

        val argumentType = argumentType.evaluate(
            typeScope = innerTypeScope,
        )

        val innerValueScope = argumentType.toStaticValueScope().chainWith(
            valueScope,
        )

        val imageType = image.inferType(
            typeScope = innerTypeScope,
            valueScope = innerValueScope,
        )

        return AbstractionType(
            argumentType = argumentType,
            imageType = imageType,
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

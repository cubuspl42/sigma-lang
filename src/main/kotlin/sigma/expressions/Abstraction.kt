package sigma.expressions

import sigma.StaticScope
import sigma.TypeExpression
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.types.AbstractionType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.values.FixedStaticValueScope
import sigma.values.tables.Scope

data class Abstraction(
    override val location: SourceLocation,
    val metaArgument: MetaArgumentExpression? = null,
    val argumentName: Symbol,
    val argumentType: TypeExpression?,
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
    }

    companion object {
        fun build(
            ctx: AbstractionContext,
        ): Abstraction = Abstraction(
            location = SourceLocation.build(ctx),
            metaArgument = ctx.metaArgument()?.let {
                MetaArgumentExpression.build(it)
            },
            argumentName = Symbol(ctx.argumentName.text),
            argumentType = ctx.argumentType?.let {
                TypeExpression.build(it)
            },
            image = Expression.build(ctx.image),
        )
    }

    override fun validate(scope: StaticScope) {
        super.validate(scope)
    }

    override fun inferType(scope: StaticScope): Type {
        val argumentType = argumentType?.evaluate(
            context = scope,
        ) ?: UndefinedType

        // TODO:
//        val argumentType = argumentType?.evaluate(
//            context = scope,
//        ) ?: throw TypeError(
//            message = "Abstraction has no declared argument type",
//        )

        val innerScope = scope.copy(
            valueScope = FixedStaticValueScope(
                entries = mapOf(
                    argumentName to argumentType,
                ),
            ).chainWith(
                scope.valueScope,
            )
        )

        return AbstractionType(
            argumentType = argumentType,
            imageType = image.inferType(scope = innerScope),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = Closure(
        context = scope,
        argumentName = argumentName,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

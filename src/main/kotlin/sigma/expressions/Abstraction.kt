package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.TypeExpression
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.MetaArgumentContext
import sigma.types.AbstractionType
import sigma.types.ArrayType
import sigma.types.MetaType
import sigma.types.TableType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.values.FixedStaticValueScope
import sigma.values.TypeError
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
            argumentName = Symbol(ctx.argumentName.text),
            argumentType = ctx.argumentType?.let {
                TypeExpression.build(it)
            },
            image = Expression.build(ctx.image),
        )
    }

    override fun validate(typeScope: StaticTypeScope, valueScope: StaticValueScope) {
        super.validate(typeScope, valueScope)
    }

    override fun inferType(typeScope: StaticTypeScope, valueScope: StaticValueScope): Type {
        val metaArgumentType = metaArgument?.let {
            it.evaluate(
                typeScope = typeScope,
            ) as? TableType ?: throw TypeError(
                location = location,
                message = "Meta-arguments have to be of table type",
            )
        } ?: TableType.Empty

        val argumentType = argumentType?.evaluate(
            typeScope = typeScope,
        ) ?: UndefinedType

        // TODO:
//        val argumentType = argumentType?.evaluate(
//            context = scope,
//        ) ?: throw TypeError(
//            message = "Abstraction has no declared argument type",
//        )

        val innerScope = FixedStaticValueScope(
            entries = mapOf(
                argumentName to argumentType,
            ),
        ).chainWith(
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
    ): Value = Closure(
        context = scope,
        argumentName = argumentName,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

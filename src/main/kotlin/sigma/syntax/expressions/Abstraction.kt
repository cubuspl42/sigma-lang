package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
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
    val metaArgument: GenericParametersTuple? = null,
    val argumentType: TupleTypeLiteral,
    val image: Expression,
) : Expression() {
    data class GenericParametersTuple(
        override val location: SourceLocation,
        val parameterNames: List<Symbol>,
    ) : Term() {
        companion object {
            fun build(
                ctx: GenericParametersTupleContext,
            ): GenericParametersTuple = GenericParametersTuple(
                location = SourceLocation.build(ctx),
                parameterNames = ctx.genericParameterDeclaration().map {
                    Symbol.of(it.name.text)
                },
            )
        }

        fun toStaticTypeScope(): StaticTypeScope = FixedStaticTypeScope(
            // TODO: Identify type variables
            entries = parameterNames.associateWith { TypeVariable },
        )
    }

    companion object {
        fun build(
            ctx: AbstractionContext,
        ): Abstraction = Abstraction(
            location = SourceLocation.build(ctx),
            metaArgument = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
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

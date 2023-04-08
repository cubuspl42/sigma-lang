package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
import sigma.semantics.types.TupleType
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteral
import sigma.syntax.typeExpressions.TypeExpression
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.syntax.Term
import sigma.values.Closure
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import sigma.values.tables.Scope

data class Abstraction(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeLiteral,
    val declaredImageType: TypeExpression? = null,
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
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeLiteral.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                TypeExpression.build(it)
            },
            image = Expression.build(ctx.image),
        )
    }

    override fun validateAdditionally(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) = enter(
        typeScope,
        valueScope,
    ) {
            _: TupleType,
            innerTypeScope: StaticTypeScope,
            innerValueScope: StaticValueScope,
        ->

        argumentType.validate(
            typeScope = innerTypeScope,
            // The outer value scope is used here on purpose
            valueScope = valueScope,
        )

        // TODO: Verify that the declared image type matches the inferred one

        declaredImageType?.validate(
            typeScope = innerTypeScope,
            valueScope = innerValueScope,
        )

        image.validate(
            typeScope = innerTypeScope,
            valueScope = innerValueScope,
        )
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = enter(
        typeScope,
        valueScope,
    ) {
            argumentType: TupleType,
            innerTypeScope: StaticTypeScope,
            innerValueScope: StaticValueScope,
        ->

        val declaredImageType = declaredImageType?.evaluate(
            typeScope = innerTypeScope,
        )

        val imageType = declaredImageType ?: image.determineType(
            typeScope = innerTypeScope,
            valueScope = innerValueScope,
        )

        return@enter UniversalFunctionType(
            argumentType = argumentType,
            imageType = imageType,
        )
    }

    private fun buildInnerTypeScope(
        typeScope: StaticTypeScope,
    ) = genericParametersTuple?.toStaticTypeScope()?.chainWith(
        backScope = typeScope,
    ) ?: typeScope

    private fun <R> enter(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
        block: (
            argumentType: TupleType,
            innerTypeScope: StaticTypeScope,
            innerValueScope: StaticValueScope,
        ) -> R,
    ): R {
        val innerTypeScope = buildInnerTypeScope(typeScope = typeScope)

        val argumentType = argumentType.evaluate(
            typeScope = innerTypeScope,
        )

        val innerValueScope = argumentType.toStaticValueScope().chainWith(
            valueScope,
        )

        return block(
            argumentType,
            innerTypeScope,
            innerValueScope,
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

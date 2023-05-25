package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
import sigma.semantics.types.TupleType
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteralBodyTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.syntax.Term
import sigma.evaluation.values.Closure
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.scope.Scope

data class AbstractionTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentBody: TupleTypeLiteralBodyTerm,
    val declaredImageType: TypeExpressionTerm? = null,
    val image: ExpressionTerm,
) : ExpressionTerm() {
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

        fun toStaticTypeScope(): TypeScope = FixedTypeScope(
            // TODO: Identify type variables
            entries = parameterNames.associateWith { TypeVariable },
        )
    }

    companion object {
        fun build(
            ctx: AbstractionContext,
        ): AbstractionTerm = AbstractionTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentBody = ctx.argumentType.let {
                TupleTypeLiteralBodyTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                TypeExpressionTerm.build(it)
            },
            image = ExpressionTerm.build(ctx.image),
        )
    }

    override fun validateAdditionally(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) = enter(
        typeScope,
        valueScope,
    ) {
            _: TupleType,
            innerTypeScope: TypeScope,
            innerValueScope: SyntaxValueScope,
        ->

        argumentBody.validate(
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
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type = enter(
        typeScope,
        valueScope,
    ) {
            argumentType: TupleType,
            innerTypeScope: TypeScope,
            innerValueScope: SyntaxValueScope,
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
        typeScope: TypeScope,
    ) = genericParametersTuple?.toStaticTypeScope()?.chainWith(
        backScope = typeScope,
    ) ?: typeScope

    private fun <R> enter(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
        block: (
            argumentType: TupleType,
            innerTypeScope: TypeScope,
            innerValueScope: SyntaxValueScope,
        ) -> R,
    ): R {
        val innerTypeScope = buildInnerTypeScope(typeScope = typeScope)

        val argumentType = argumentBody.evaluate(
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
        argumentType = argumentBody,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

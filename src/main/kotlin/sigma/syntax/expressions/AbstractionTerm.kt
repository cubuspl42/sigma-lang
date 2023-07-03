package sigma.syntax.expressions

import sigma.TypeScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeConstructorTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.semantics.types.TypeVariable
import sigma.syntax.Term
import sigma.evaluation.values.Closure
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.scope.Scope

data class AbstractionTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple? = null,
    val argumentType: TupleTypeConstructorTerm,
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

        fun toStaticTypeScope(
            typeScope: TypeScope,
        ): TypeScope = FixedTypeScope(
            entries = parameterNames.associateWith { TypeVariable(name = it) },
        ).chainWith(
            backScope = typeScope,
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
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                TypeExpressionTerm.build(it)
            },
            image = ExpressionTerm.build(ctx.image),
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

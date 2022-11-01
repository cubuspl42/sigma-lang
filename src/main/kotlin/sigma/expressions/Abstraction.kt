package sigma.expressions

import sigma.StaticScope
import sigma.TypeExpression
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.types.AbstractionType
import sigma.types.Type
import sigma.types.UndefinedType
import sigma.values.FixedStaticValueScope
import sigma.values.TypeError
import sigma.values.tables.Scope

data class Abstraction(
    val argumentName: Symbol,
    val argumentType: TypeExpression?,
    val image: Expression,
) : Expression() {
    companion object {
        fun build(
            abstraction: AbstractionContext,
        ): Abstraction = Abstraction(
            argumentName = Symbol(abstraction.argumentName.text),
            argumentType = abstraction.argumentType?.let {
                TypeExpression.build(it)
            },
            image = Expression.build(abstraction.image),
        )
    }

    override fun validate(scope: StaticScope) {
        super.validate(scope)
    }

    override fun inferType(scope: StaticScope): Type {
        val argumentType = argumentType?.evaluate(
            context = scope,
        ) ?: return UndefinedType

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

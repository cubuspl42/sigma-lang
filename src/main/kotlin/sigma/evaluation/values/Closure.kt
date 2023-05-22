package sigma.evaluation.values

import sigma.Thunk
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TupleTypeLiteralTerm
import sigma.evaluation.values.tables.Scope
import sigma.evaluation.values.tables.Table
import sigma.evaluation.values.tables.chainWith

class Closure(
    private val context: Scope,
    private val argumentType: TupleTypeLiteralTerm,
    private val image: ExpressionTerm,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Thunk = image.evaluate(
        scope = argumentType.toArgumentScope(
            argument = argument as Table,
        ).chainWith(
            context = context,
        ),
    )

    override fun dump(): String = "(closure)"
}

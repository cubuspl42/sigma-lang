package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Value
import sigma.semantics.Computation
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.ArrayTypeConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.FunctionTypeTerm
import sigma.syntax.expressions.TupleTypeConstructorTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm

class FunctionTypeConstructor(
    override val term: ExpressionTerm,
    val argumentType: TupleTypeConstructor,
    val imageType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: FunctionTypeTerm,
        ): FunctionTypeConstructor = FunctionTypeConstructor(
            term = term,
            argumentType = TupleTypeConstructor.build(
                declarationScope = declarationScope,
                term = term.argumentType,
            ),
            imageType = Expression.build(
                declarationScope = declarationScope,
                term = term.imageType,
            ),
        )
    }

    override val inferredType: Computation<Type>
        get() = TODO()

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }

    override fun evaluateDirectly(
        context: EvaluationContext,
        scope: Scope,
    ): Value = UniversalFunctionType(
        argumentType = argumentType.evaluate(
            context,
            scope,
        ) as TupleType,
        imageType = imageType.evaluate(
            context,
            scope,
        ) as Type,
    )
}

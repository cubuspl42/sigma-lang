package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk
import sigma.evaluation.values.evaluateInitialValue
import sigma.semantics.Formula
import sigma.semantics.StaticScope
import sigma.semantics.SemanticError
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.semantics.types.UniversalFunctionType
import sigma.syntax.expressions.FunctionTypeConstructorTerm

class FunctionTypeConstructor(
    override val term: FunctionTypeConstructorTerm,
    val argumentType: TupleTypeConstructor,
    val imageType: Expression,
) : Expression() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: FunctionTypeConstructorTerm,
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

    override val inferredType: Thunk<Type>
        get() = TODO()

    override fun bind(
        scope: Scope,
    ): Thunk<Value> = UniversalFunctionType(
        genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
        argumentType = argumentType.bind(
            scope = scope,
        ).evaluateInitialValue() as TupleType,
        imageType = imageType.bind(
            scope = scope,
        ).evaluateInitialValue() as Type,
    ).asThunk

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}

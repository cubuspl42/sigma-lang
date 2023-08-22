package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorSourceTerm

class FunctionTypeConstructor(
    override val term: FunctionTypeConstructorSourceTerm,
    val argumentType: TupleTypeConstructor,
    val imageType: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: FunctionTypeConstructorSourceTerm,
        ): FunctionTypeConstructor = FunctionTypeConstructor(
            term = term,
            argumentType = TupleTypeConstructor.build(
                outerScope = outerScope,
                term = term.argumentType,
            ),
            imageType = Expression.build(
                outerScope = outerScope,
                term = term.imageType,
            ),
        )
    }

    override val inferredType: Thunk<Type>
        get() = TODO()

    override fun bind(scope: Scope): Thunk<Value> = Thunk.combine2(
        argumentType.bind(
            scope = scope,
        ),
        imageType.bind(
            scope = scope,
        ),
    ) { argumentType, imageType ->
        UniversalFunctionType(
            genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
            argumentType = argumentType as TupleType,
            imageType = imageType as Type,
        )
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }
}

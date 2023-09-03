package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorTerm

class FunctionTypeConstructor(
    override val outerScope: StaticScope,
    override val term: FunctionTypeConstructorTerm,
    val argumentType: TupleTypeConstructor,
    val imageType: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: FunctionTypeConstructorTerm,
        ): FunctionTypeConstructor = FunctionTypeConstructor(
            outerScope = outerScope,
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

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = Thunk.combine2(
        argumentType.bind(
            dynamicScope = dynamicScope,
        ),
        imageType.bind(
            dynamicScope = dynamicScope,
        ),
    ) { argumentType, imageType ->
        UniversalFunctionType(
            genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
            argumentType = argumentType.asType as TupleType,
            imageType = imageType.asType as Type,
        ).asValue
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
        )
    }

    override val subExpressions: Set<Expression> = setOf(argumentType, imageType)
}

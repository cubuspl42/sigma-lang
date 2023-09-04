package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class UserVariableDefinition(
    override val outerScope: StaticScope,
    private val term: LocalDefinitionTerm,
) : UserDefinition(), ResolvableDeclaration {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: LocalDefinitionTerm,
        ): UserVariableDefinition = UserVariableDefinition(
            outerScope = declarationScope,
            term = term,
        )
    }

    override val name: Symbol = term.name

    override val declaredTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            Expression.build(
                outerScope = outerScope,
                term = it,
            )
        }
    }

    override val body: Expression by lazy {
        Expression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    override val resolvedType: Thunk<Type> by lazy { effectiveValueType }

    override val expressionClassification: ExpressionClassification = VariableClassification(
        resolvedFormula = Formula(name = name),
    )
}

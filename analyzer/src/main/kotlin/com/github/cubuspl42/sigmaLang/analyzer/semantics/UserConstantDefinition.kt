package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class ReverseTranslationScope(
    private val staticScope: StaticScope,
) : DynamicScope {
    override fun getValue(name: Symbol): Thunk<Value>? =
        staticScope.resolveName(name = name)?.let { resolvableDeclaration ->
            if (resolvableDeclaration is ConstantDefinition) {
                resolvableDeclaration.valueThunk
            } else {
                null
            }
        }
}

class UserConstantDefinition private constructor(
    private val outerScope: StaticScope,
    term: ConstantDefinitionTerm,
    private val userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        outerScope = outerScope,
        term = term,
    ),
) : ConstantDefinition(), Declaration by userDefinition {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ConstantDefinitionTerm,
        ): UserConstantDefinition = UserConstantDefinition(
            outerScope = outerScope,
            term = term,
        )
    }

    override val valueThunk: Thunk<Value> by lazy {
        userDefinition.body.bind(
            dynamicScope = ReverseTranslationScope(
                staticScope = outerScope,
            ),
        )
    }

    val expressionMap: ExpressionMap
        get() = userDefinition.body.expressionMap

    val errors: Set<SemanticError>
        get() = userDefinition.body.errors
}

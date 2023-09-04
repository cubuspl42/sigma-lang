package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class UserConstantDefinition private constructor(
    private val outerScope: StaticScope,
    term: ConstantDefinitionTerm,
    private val userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        outerScope = outerScope,
        term = term,
    ),
) : ConstantDefinition(), UserDefinition by userDefinition {
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

    override val errors: Set<SemanticError>
        get() = userDefinition.body.errors
}

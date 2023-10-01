package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class UserConstantDefinition private constructor(
    private val outerScope: StaticScope,
    term: ConstantDefinitionTerm,
    private val userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        outerScope = outerScope,
        term = term,
    ),
) : ConstantDefinition(), AssignmentDefinition by userDefinition {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ConstantDefinitionTerm,
        ): UserConstantDefinition = UserConstantDefinition(
            outerScope = outerScope,
            term = term,
        )
    }

    override val errors: Set<SemanticError>
        get() = userDefinition.assignedBody.directErrors
}

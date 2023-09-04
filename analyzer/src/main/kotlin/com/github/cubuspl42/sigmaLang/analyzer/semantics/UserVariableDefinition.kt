package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class UserVariableDefinition private constructor(
    outerScope: StaticScope,
    term: LocalDefinitionTerm,
    userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        outerScope = outerScope,
        term = term,
    ),
) : VariableDeclaration(), UserDefinition by userDefinition {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: LocalDefinitionTerm,
        ): UserVariableDefinition = UserVariableDefinition(
            outerScope = declarationScope,
            term = term,
        )
    }
}

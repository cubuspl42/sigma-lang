package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class UserVariableDefinition(
    outerScope: StaticScope,
    term: LocalDefinitionTerm,
    userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        outerScope = outerScope,
        term = term,
    ),
) : VariableDeclaration(), UserDefinition by userDefinition, ResolvableDeclaration {
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

package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class UserVariableDefinition private constructor(
    context: Expression.BuildContext,
    term: LocalDefinitionTerm,
    userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        context = context,
        term = term,
    ),
) : EmbodiedUserDefinition by userDefinition {
    companion object {
        fun build(
            context: Expression.BuildContext,
            term: LocalDefinitionTerm,
        ): UserVariableDefinition = UserVariableDefinition(
            context = context,
            term = term,
        )
    }
}

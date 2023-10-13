package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class UserConstantDefinition private constructor(
    private val context: Expression.BuildContext,
    term: ConstantDefinitionTerm,
    private val userDefinition: UserDefinitionMixin = UserDefinitionMixin(
        context = context,
        term = term,
    ),
) : ConstantDefinition(), EmbodiedUserDefinition by userDefinition {
    companion object {
        fun build(
            context: Expression.BuildContext,
            term: ConstantDefinitionTerm,
        ): UserConstantDefinition = UserConstantDefinition(
            context = context,
            term = term,
        )

    }

//    override val valueThunk: Thunk<Value> by lazy {
//        userDefinition.body.bind(
//            dynamicScope = ReverseTranslationScope(
//                staticScope = context.outerScope,
//            ),
//        )
//    }

    override val errors: Set<SemanticError>
        get() = userDefinition.body.directErrors
}

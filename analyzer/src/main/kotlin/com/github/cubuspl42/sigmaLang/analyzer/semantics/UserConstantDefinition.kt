package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class UserConstantDefinition(
    private val containingNamespaceDefinition: NamespaceDefinition,
    val term: ConstantDefinitionTerm,
) : ConstantDefinition() {
    companion object {
        fun build(
            containingNamespaceDefinition: NamespaceDefinition,
            term: ConstantDefinitionTerm,
        ): UserConstantDefinition = UserConstantDefinition(
            containingNamespaceDefinition = containingNamespaceDefinition,
            term = term,
        )
    }

    inner class ConstantValueDefinition : ValueDefinition() {
        override val name: Symbol
            get() = this@UserConstantDefinition.name

        override val outerScope: StaticScope
            get() = containingNamespaceDefinition.innerStaticScope

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
                outerScope = containingNamespaceDefinition.innerStaticScope,
                term = term.body,
            )
        }
    }

    val asValueDefinition = ConstantValueDefinition()

    override val valueThunk by lazy {
        asValueDefinition.body.bind(
            dynamicScope = containingNamespaceDefinition.innerDynamicScope,
        )
    }

    override val effectiveType: Thunk<Type>
        get() = asValueDefinition.effectiveValueType

    override val expressionMap: ExpressionMap
        get() = asValueDefinition.body.expressionMap

    override val name: Symbol
        get() = term.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}

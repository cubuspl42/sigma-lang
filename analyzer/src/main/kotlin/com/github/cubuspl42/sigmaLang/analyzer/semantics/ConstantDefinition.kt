package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionSourceTerm

class ConstantDefinition(
    private val containingNamespace: Namespace,
    val term: ConstantDefinitionSourceTerm,
) : StaticDefinition() {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: ConstantDefinitionSourceTerm,
        ): ConstantDefinition = ConstantDefinition(
            containingNamespace = containingNamespace,
            term = term,
        )
    }

    inner class ConstantValueDefinition : ValueDefinition() {
        override val name: Symbol
            get() = this@ConstantDefinition.name

        override val declarationScope: StaticScope
            get() = containingNamespace.innerDeclarationScope

        override val declaredTypeBody: Expression? by lazy {
            term.declaredTypeBody?.let {
                Expression.build(
                    declarationScope = declarationScope,
                    term = it,
                )
            }
        }

        override val body: Expression by lazy {
            Expression.build(
                declarationScope = containingNamespace.innerDeclarationScope,
                term = term.body,
            )
        }
    }

    val asValueDefinition = ConstantValueDefinition()


    val valueThunk by lazy {
        asValueDefinition.body.bind(
            scope = containingNamespace.innerScope,
        )
    }

    override val staticValue: Thunk<Value>
        get() = this.valueThunk

    override val name: Symbol
        get() = term.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}
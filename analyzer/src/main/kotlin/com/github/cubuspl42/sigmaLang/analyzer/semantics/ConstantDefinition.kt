package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm

class ConstantDefinition(
    private val containingNamespace: Namespace,
    val term: ConstantDefinitionTerm,
) : NamespaceEntry() {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: ConstantDefinitionTerm,
        ): ConstantDefinition = ConstantDefinition(
            containingNamespace = containingNamespace,
            term = term,
        )
    }

    inner class ConstantValueDefinition : ValueDefinition() {
        override val name: Symbol
            get() = this@ConstantDefinition.name

        override val outerScope: StaticScope
            get() = containingNamespace.innerStaticScope

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
                outerScope = containingNamespace.innerStaticScope,
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

    fun evaluateResult(): EvaluationOutcome<Value> = valueThunk.evaluateInitial()

    override val staticValue: Thunk<Value>
        get() = this.valueThunk

    override val expressionMap: ExpressionMap
        get() = asValueDefinition.body.expressionMap

    override val name: Symbol
        get() = term.name

    override val errors: Set<SemanticError>
        get() = asValueDefinition.errors
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm

class UserDefinitionMixin(
    private val outerScope: StaticScope,
    private val term: DefinitionTerm,
) : Declaration {
    private val annotatedTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            Expression.build(
                outerScope = outerScope,
                term = it,
            )
        }
    }

    private val annotatedType: Thunk<Type>? by lazy {
        annotatedTypeBody?.let { expression ->
            expression.bind(dynamicScope = BuiltinScope).thenJust { it.asType!! }
        }
    }

    val body: Expression by lazy {
        Expression.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    override val name: Symbol
        get() = term.name

    override val declaredTypeThunk: Thunk<Type> by lazy {
        annotatedType ?: body.inferredType
    }
}

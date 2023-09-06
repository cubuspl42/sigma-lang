package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions


import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.StringLiteralTerm

data class StringLiteral(
    override val term: StringLiteralTerm,
    override val outerScope: StaticScope,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: StringLiteralTerm,
        ): StringLiteral = StringLiteral(
            outerScope = outerScope,
            term = term,
        )
    }

    val value: StringValue
        get() = term.value

    override val inferredType: Thunk<Type> = Thunk.pure(StringType)

    override val subExpressions: Set<Expression> = emptySet()

    override val errors: Set<SemanticError> = emptySet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = value.toThunk()
}

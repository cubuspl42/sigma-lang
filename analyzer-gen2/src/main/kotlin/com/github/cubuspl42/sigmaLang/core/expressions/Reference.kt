package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext

sealed class Reference : Expression() {
    final override val subExpressions: Set<Expression> = emptySet()
}

sealed class SpecialReference : Reference()

data object RootReference : SpecialReference() {
    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation {
        TODO("Not yet implemented")
    }

    override fun bind(
        context: DynamicContext,
    ): Lazy<Value> = context.rootLazy
}

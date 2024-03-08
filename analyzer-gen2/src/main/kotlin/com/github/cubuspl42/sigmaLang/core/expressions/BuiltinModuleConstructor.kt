package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.BuiltinModule
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.typeNameOf

data object BuiltinModuleConstructor : Expression() {
    val builtinModuleTypeName = typeNameOf<BuiltinModule>()

    override val subExpressions: Set<Expression> = emptySet()

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : Expression.CodegenRepresentation() {
        override fun generateCode(): CodeBlock = CodeBlock.of("%T", builtinModuleTypeName)
    }

    override fun bind(
        scope: DynamicScope,
    ): Lazy<Value> = lazyOf(BuiltinModule)
}

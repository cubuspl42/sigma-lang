package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.Module.CodegenRepresentationContext.Companion
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec

private fun <K, V : Any> MutableMap<K, V>.update(expression: K, function: (oldValue: V?) -> V) {
    val newValue = function(get(expression))
    put(expression, newValue)
}

interface CompilationAnalysisContext {

    fun getSubExpressions(
        abstractionConstructor: AbstractionConstructor,
    ): Set<Expression>

    fun getReferenceCount(
        expression: Expression,
    ): Int
}

class MutableCompilationAnalysisContext : CompilationAnalysisContext {
    private val analyzedExpressions = mutableSetOf<Expression>()

    private val subExpressionsByAbstraction = mutableMapOf<AbstractionConstructor, Set<Expression>>()

    private val referenceCountByExpression = mutableMapOf<Expression, Int>()

    fun markAnalyzed(
        expression: Expression,
    ) {
        analyzedExpressions.add(expression)
    }

    fun wasAnalyzed(
        expression: Expression,
    ): Boolean = expression in analyzedExpressions

    fun registerSubExpression(
        abstractionConstructor: AbstractionConstructor,
        subExpression: Expression,
    ) {
        subExpressionsByAbstraction.update(abstractionConstructor) { oldValue ->
            if (oldValue == null) {
                setOf(subExpression)
            } else {
                oldValue + subExpression
            }
        }
    }

    override fun getSubExpressions(
        abstractionConstructor: AbstractionConstructor,
    ): Set<Expression> = subExpressionsByAbstraction[abstractionConstructor] ?: emptySet()

    fun registerReference(
        referredExpression: Expression,
    ) {
        referenceCountByExpression.update(referredExpression) { oldValue ->
            if (oldValue == null) {
                1
            } else {
                oldValue + 1
            }
        }
    }

    override fun getReferenceCount(
        expression: Expression,
    ): Int = referenceCountByExpression[expression] ?: 0
}

class CompilationCodegenContext {
    private var nextId = 0

    fun getUniqueIdentifier(): String {
        val id = nextId
        nextId += 1
        return "v$id"
    }
}

sealed class Expression {
    sealed class OuterCodegenRepresentation {
        abstract val innerRepresentation: InnerCodegenRepresentation

        abstract fun generateUsage(): CodeBlock
    }

    class NamedCodegenRepresentation(
        val name: String,
        val initializer: InnerCodegenRepresentation,
    ) : OuterCodegenRepresentation() {
        override val innerRepresentation: InnerCodegenRepresentation = initializer

        override fun generateUsage(): CodeBlock = CodeBlock.of(name)

        fun generateDefinition(): PropertySpec = PropertySpec.builder(
            name = name,
            type = Module.CodegenRepresentationContext.lazyValueTypeName,
        ).initializer(
            initializer.generateCode()
        ).build()
    }

    class InlineCodegenRepresentation(
        val body: InnerCodegenRepresentation,
    ) : OuterCodegenRepresentation() {
        override val innerRepresentation: InnerCodegenRepresentation = body

        override fun generateUsage(): CodeBlock = body.generateCode()
    }

    abstract class InnerCodegenRepresentation {
        abstract fun generateCode(): CodeBlock
    }

    abstract val subExpressions: Set<Expression>

    fun buildOuterCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): OuterCodegenRepresentation {
        val module = context.module

        val referenceCount = module.getReferenceCount(this)

        val innerRepresentation = buildInnerCodegenRepresentation(
            context = context,
        )

        return if (referenceCount <= 1) {
            InlineCodegenRepresentation(
                body = innerRepresentation,
            )
        } else {
            val name = context.generateUniqueName()

            NamedCodegenRepresentation(
                name = name,
                initializer = innerRepresentation,
            )
        }
    }

    abstract fun buildInnerCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): InnerCodegenRepresentation

    abstract fun bind(
        scope: DynamicScope,
    ): Lazy<Value>
}

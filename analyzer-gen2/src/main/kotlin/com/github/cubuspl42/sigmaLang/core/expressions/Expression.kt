package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

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

sealed class Expression : ShadowExpression() {
    data class BuildContext(
        val builtin: Expression,
    ) {
        fun referBuiltin(
            name: Identifier,
        ): Expression = builtin.readField(
            fieldName = name,
        )
    }

    abstract class CodegenRepresentation {
        abstract fun generateCode(): CodeBlock
    }

    abstract val subExpressions: Set<Expression>

    abstract fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation

    abstract fun bind(
        scope: DynamicScope,
    ): Lazy<Value>

    final override val rawExpression: Expression
        get() = this

    fun bindStrict(
        scope: DynamicScope,
    ): Value = bind(
        scope = scope,
    ).value

    fun readField(
        fieldName: Identifier,
    ): Call = Call(
        calleeLazy = lazyOf(this),
        passedArgumentLazy = lazyOf(fieldName.toLiteral()),
    )

    fun call(
        passedArgument: Expression,
    ): Call = Call(
        calleeLazy = lazyOf(this),
        passedArgumentLazy = lazyOf(passedArgument),
    )
}

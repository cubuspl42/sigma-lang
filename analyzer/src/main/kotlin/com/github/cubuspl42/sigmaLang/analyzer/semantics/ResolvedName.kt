package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.FieldRead
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.IntLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Reference
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition

sealed interface ResolvedName {
    fun buildReference(): Expression
}

sealed class ResolvedAbstractionArgument : ResolvedName {
    abstract val argumentDeclaration: AbstractionConstructor.ArgumentDeclaration
}

class ResolvedOrderedArgument(
    val index: Long,
    override val argumentDeclaration: AbstractionConstructor.ArgumentDeclaration,
) : ResolvedAbstractionArgument() {
    override fun buildReference(): Expression = Call(
        subjectLazy = lazy { Reference(referredDeclaration = argumentDeclaration) },
        argumentLazy = lazy { IntLiteral.of(index) },
    )

}

class ResolvedUnorderedArgument(
    val name: Identifier,
    override val argumentDeclaration: AbstractionConstructor.ArgumentDeclaration,
) : ResolvedAbstractionArgument() {
    override fun buildReference(): Expression = FieldRead(
        subjectLazy = lazy { Reference(referredDeclaration = argumentDeclaration) },
        fieldName = name,
    )
}

class ResolvedDefinition(
    val definition: Definition,
) : ResolvedName {
    val body: Expression
        get() = definition.body

    override fun buildReference(): Expression = body
}

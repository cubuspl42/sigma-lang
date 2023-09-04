package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface UserDefinition : UserDeclaration {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: Type.MatchResult,
    ) : SemanticError

    val body: Expression
}

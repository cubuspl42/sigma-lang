package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface UserDefinition : UserIntroduction {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: Type.MatchResult,
    ) : SemanticError
}

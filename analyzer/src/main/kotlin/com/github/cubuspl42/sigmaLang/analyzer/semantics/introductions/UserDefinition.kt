package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface UserDefinition : UserIntroduction, Definition {
    data class UnmatchedInferredTypeError(
        override val location: SourceLocation?,
        val matchResult: MembershipType.MatchResult,
    ) : SemanticError
}

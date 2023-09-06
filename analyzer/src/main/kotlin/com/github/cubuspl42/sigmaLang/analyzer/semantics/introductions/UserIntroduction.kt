package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError

interface UserIntroduction : Introduction {
    val errors: Set<SemanticError>
}

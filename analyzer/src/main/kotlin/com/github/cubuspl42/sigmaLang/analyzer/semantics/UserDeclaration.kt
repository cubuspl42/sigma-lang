package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

interface UserDeclaration : Declaration {
    val errors: Set<SemanticError>

    val annotatedTypeThunk: Thunk<Type>?
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

interface NamedDeclaration {
    val name: Symbol
}

interface Declaration : NamedDeclaration {
    val effectiveTypeThunk: Thunk<Type>
}

// TODO: Sealed
interface UserDeclaration : Declaration {
    val errors: Set<SemanticError>

    val annotatedTypeThunk: Thunk<Type>?
}

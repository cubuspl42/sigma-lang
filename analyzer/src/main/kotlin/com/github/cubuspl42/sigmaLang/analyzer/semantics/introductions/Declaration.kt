package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

interface Declaration : VariableIntroduction, AnnotatableIntroduction {
    override val annotatedTypeThunk: Thunk<Type>

    override val effectiveTypeThunk: Thunk<Type>
        get() = annotatedTypeThunk
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TraitValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object TraitTranslationScope : DynamicScope {
    override fun getValue(
        declaration: Declaration,
    ): Thunk<Value> = Thunk.pure(
        TraitValue(
            traitDeclaration = declaration,
            traitType = declaration.declaredType as TupleType,
            path = TypeVariable.Path.Root,
        )
    )
}

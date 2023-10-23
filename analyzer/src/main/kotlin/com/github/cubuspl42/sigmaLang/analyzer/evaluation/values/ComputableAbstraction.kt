package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.chainWith
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

class ComputableAbstraction(
    private val outerDynamicScope: DynamicScope,
    private val argumentDeclaration: ArgumentDeclaration,
    private val image: Expression,
) : Abstraction() {
    override fun apply(
        argument: Value,
    ): Thunk<Value> {
        val argumentScope = object : DynamicScope {
            override fun getValue(
                declaration: Declaration,
            ): Thunk<Value>? = if (argumentDeclaration == declaration) {
                Thunk.pure(argument)
            } else {
                null
            }
        }

        return image.bind(
            dynamicScope = argumentScope.chainWith(
                context = outerDynamicScope,
            ),
        )
    }
}

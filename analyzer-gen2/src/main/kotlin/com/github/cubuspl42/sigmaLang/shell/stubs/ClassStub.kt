package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.concepts.ClassBuilder
import com.github.cubuspl42.sigmaLang.core.concepts.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.concepts.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ExpressionScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

object ClassStub {
    data class MethodDefinitionStub(
        val name: Identifier,
        val methodConstructorStub: AbstractionConstructorStub,
    ) {
        fun transform(
            context: FormationContext,
        ) = object : ClassBuilder.MethodDefinitionBuilder(
            name = name,
        ) {
            override fun buildImplementation(
                thisReference: Expression,
            ): ExpressionBuilder<AbstractionConstructor> {
                val innerContext = context.copy(
                    scope = ExpressionScope(
                        name = Identifier(name = "this"),
                        boundExpression = thisReference,
                    ).chainWith(
                        context.scope,
                    ),
                )

                return methodConstructorStub.transform(
                    context = innerContext,
                )
            }
        }
    }

    fun of(
        constructorName: Identifier,
        methodDefinitionStubs: Set<MethodDefinitionStub>,
    ): ExpressionStub<ClassBuilder.Constructor> = object : ExpressionStub<ClassBuilder.Constructor>() {
        override fun transform(
            context: FormationContext,
        ) = object : ClassBuilder(
            constructorName = constructorName,
        ) {
            override fun buildMethods(
                classReference: Reference,
            ): Set<MethodDefinitionBuilder> = methodDefinitionStubs.mapUniquely { methodDefinitionStub ->
                methodDefinitionStub.transform(context = context)
            }
        }
    }
}

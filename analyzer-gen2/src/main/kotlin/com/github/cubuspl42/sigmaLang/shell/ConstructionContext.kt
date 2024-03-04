package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope

data class ConstructionContext(
    val scope: StaticScope,
    val moduleRoot: Lazy<AbstractionConstructor>,
) {
    fun referBuiltin(name: Identifier): Lazy<Expression> {
        return Call.fieldRead(
            subjectLazy = Call.fieldRead(
                subjectLazy = lazyOf(
                    ArgumentReference(
                        referredAbstractionLazy = moduleRoot,
                    ),
                ),
                readFieldName = Identifier(
                    name = "builtin",
                ),
            ),
            readFieldName = name,
        )
    }

    data class IfExpression(
        val calleeLazy: Lazy<Expression>,
    ) {
        fun constructCall(
            condition: Lazy<Expression>,
            thenCase: Lazy<Expression>,
            elseCase: Lazy<Expression>,
        ): Lazy<Call> = lazyOf(
            Call(
                calleeLazy = calleeLazy,
                passedArgumentLazy = lazyOf(
                    UnorderedTupleConstructor(
                        valueByKey = mapOf(
                            Identifier(name = "condition") to condition,
                            Identifier(name = "then") to thenCase,
                            Identifier(name = "else") to elseCase,
                        ),
                    ),
                ),
            ),
        )
    }

    fun referIf(): IfExpression = IfExpression(
        calleeLazy = referBuiltin(
            name = Identifier(name = "if"),
        ),
    )

    fun buildPanicCall(): Lazy<Call> = lazyOf(
        Call(
            calleeLazy = referBuiltin(
                name = Identifier(name = "panic"),
            ),
            passedArgumentLazy = lazyOf(
                UnorderedTupleConstructor.Empty,
            ),
        ),
    )
}

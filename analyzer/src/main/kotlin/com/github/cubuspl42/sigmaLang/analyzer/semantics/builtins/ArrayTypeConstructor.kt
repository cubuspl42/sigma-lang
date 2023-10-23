package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Reference
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

object ArrayTypeConstructor : WrapperTypeConstructor() {
    fun build(
        context: Expression.BuildContext,
        term: ArrayTypeConstructorTerm,
    ): Stub<Call> {
        val subjectStub = Reference.build(
            context,
            referredName = Name,
        )

        return object : Stub<Call> {
            override val resolved: Call = object : Call() {
                override val term: ExpressionTerm? = null

                override val subject: Expression by lazy { subjectStub.resolved }

                override val argument: Expression by lazy {
                    Expression.build(
                        context = context,
                        term = term.elementType,
                    ).resolved
                }

                override val outerScope: StaticScope = context.outerScope
            }
        }
    }

    object Name : Symbol() {
        override fun dump(): String = "#array-type-constructor"
    }

    override val argumentType: TableType = ArrayType(elementType = TypeType)

    override fun wrapType(wrappedType: TypeAlike): SpecificType {
        return ArrayType(
            elementType = wrappedType,
        )
    }

    override fun dump(): String = "(array type constructor)"
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Reference
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.ArrayType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

object ArrayTypeConstructor : WrapperTypeConstructor() {
    fun build(
        context: Expression.BuildContext,
        term: ArrayTypeConstructorTerm,
    ): Expression.Stub<Call> = object : Expression.Stub<Call> {
        override val resolved: Call = object : Call() {
            override val term: ExpressionTerm? = null

            override val subject: Expression = object : Reference() {
                override val term: ReferenceTerm? = null

                override val referredName: Symbol = Name

                override val outerScope: StaticScope = context.outerScope
            }

            override val argument: Expression by lazy {
                Expression.build(
                    context = context,
                    term = term.elementType,
                ).resolved
            }

            override val outerScope: StaticScope = context.outerScope
        }
    }

    object Name : Symbol() {
        override fun dump(): String = "#array-type-constructor"
    }

    override val argumentType: TableType = ArrayType(elementType = TypeType)

    override fun wrapType(wrappedType: MembershipType): MembershipType {
        return ArrayType(
            elementType = wrappedType,
        )
    }

    override fun dump(): String = "(array type constructor)"
}

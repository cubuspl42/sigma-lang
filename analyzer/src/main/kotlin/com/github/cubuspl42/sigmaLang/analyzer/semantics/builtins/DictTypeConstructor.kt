package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Reference
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

object DictTypeConstructor : TypeConstructor() {
    fun build(
        context: Expression.BuildContext,
        term: DictTypeConstructorTerm,
    ): Expression.Stub<Call> = object : Expression.Stub<Call> {
        override val resolved: Call = object : Call() {
            override val term: ExpressionTerm? = null

            override val subject: Expression = object : Reference() {
                override val term: ReferenceTerm? = null

                override val referredName: Symbol = Name

                override val outerScope: StaticScope = context.outerScope
            }

            override val argument: Expression by lazy {
                object : UnorderedTupleConstructor() {
                    override val term: UnorderedTupleConstructorTerm? = null

                    override val entries: Set<Entry> = setOf(
                        object : Entry() {
                            override val name: Symbol = KeyTypeName

                            override val value: Expression by lazy {
                                Expression.build(
                                    context = context,
                                    term = term.keyType,
                                ).resolved
                            }
                        },
                        object : Entry() {
                            override val name: Symbol = ValueTypeName

                            override val value: Expression by lazy {
                                Expression.build(
                                    context = context,
                                    term = term.valueType,
                                ).resolved
                            }
                        },
                    )

                    override val outerScope: StaticScope = context.outerScope
                }
            }

            override val outerScope: StaticScope = context.outerScope
        }
    }

    object Name : Symbol() {
        override fun dump(): String = "#dict-type-constructor"
    }

    object KeyTypeName : Symbol() {
        override fun dump(): String = "#key-type"
    }

    object ValueTypeName : Symbol() {
        override fun dump(): String = "#value-type"
    }

    override val argumentType: TableType = UnorderedTupleType(
        valueTypeByName = mapOf(
            KeyTypeName to TypeType,
            ValueTypeName to TypeType,
        ),
    )

    override fun applyType(argument: Value): MembershipType {
        val arguments = argument as DictValue

        val keyType = arguments.read(key = KeyTypeName)!!.asType!!
        val valueType = arguments.read(key = ValueTypeName)!!.asType!!

        return DictType(
            keyType = keyType,
            valueType = valueType,
        )
    }

    override fun dump(): String = "(dict type constructor)"
}

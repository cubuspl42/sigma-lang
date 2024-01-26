package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

object DictTypeConstructor : TypeConstructorConstructor() {
    fun build(
        context: Expression.BuildContext,
        term: DictTypeConstructorTerm,
    ): Stub<Call> {
        val buildOutput = ReferenceTerm.build(
            context,
            term = term,
            referredName = Name,
        )

        return object : Stub<Call> {
            override val resolved: Call = object : Call() {
                override val term: ExpressionTerm? = null

                override val subject: Expression by buildOutput.expressionLazy

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

    override fun applyType(argument: Value): TypeAlike {
        val arguments = argument as DictValue

        val keyType = arguments.readValue(key = KeyTypeName)!!.asType!!
        val valueType = arguments.readValue(key = ValueTypeName)!!.asType!!

        return DictType(
            keyType = keyType,
            valueType = valueType,
        )
    }

    override fun dump(): String = "(dict type constructor)"
}

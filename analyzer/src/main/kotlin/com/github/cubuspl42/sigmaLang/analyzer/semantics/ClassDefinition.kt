package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm

class ClassDefinition(
    private val outerScope: StaticScope,
    private val term: ClassDefinitionTerm,
) : ConstantDefinition() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: ClassDefinitionTerm,
        ): ClassDefinition = ClassDefinition(
            outerScope = outerScope,
            term = term,
        )
    }

    override val name: Symbol
        get() = term.name

    private val body by lazy {
        UnorderedTupleTypeConstructor.build(
            outerScope = outerScope,
            term = term.body,
        )
    }

    private val bodyType: UnorderedTupleType by lazy {
        body.bindTranslated(staticScope = outerScope).value!!.asType as UnorderedTupleType
    }

    private val instanceType: UnorderedTupleType by lazy { bodyType }

    override val valueThunk: Thunk<Value> by lazy {
        Thunk.pure(
            DictValue(
                entries = mapOf(
                    Symbol.of("type") to instanceType.asValue,
                    Symbol.of("new") to object : ComputableFunctionValue() {
                        override fun apply(argument: Value): Thunk<Value> {
                            argument as DictValue
                            return Thunk.pure(argument) // TODO: Tag
                        }
                    },
                )
            )
        )
    }

    override val effectiveTypeThunk: Thunk<Type> by lazy {
        Thunk.pure(
            UnorderedTupleType(
                valueTypeByName = mapOf(
                    Symbol.of("type") to MetaType,
                    Symbol.of("new") to UniversalFunctionType(
                        argumentType = bodyType,
                        imageType = instanceType,
                    ),
                )
            ),
        )
    }
}

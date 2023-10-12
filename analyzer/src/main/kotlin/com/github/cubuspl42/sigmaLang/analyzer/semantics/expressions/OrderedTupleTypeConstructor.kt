package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm

abstract class OrderedTupleTypeConstructor : TupleTypeConstructor() {
    abstract override val term: OrderedTupleTypeConstructorTerm

    abstract val elements: List<Element>

    abstract class Element {
        abstract val name: Symbol?

        abstract val type: Expression

        companion object {
            fun build(
                context: BuildContext,
                element: OrderedTupleTypeConstructorTerm.Element,
            ): Stub<Element> {
                val typeStub = Expression.build(
                    context = context,
                    term = element.type,
                )

                return object : Stub<Element> {
                    override val resolved: Element by lazy {
                        object : Element() {
                            override val name: Symbol? = element.name

                            override val type: Expression by lazy { typeStub.resolved }
                        }
                    }
                }
            }
        }

        data class Analysis(
            val name: Symbol?,
            val typeAnalysis: Expression.Analysis,
        )

        val classifiedElement: ClassificationContext<OrderedTupleType.Element>
            get() = type.classifiedValue.transform { typeValue ->
                OrderedTupleType.Element(
                    name = name,
                    type = typeValue.asType!!,
                )
            }
    }

    companion object {
        fun build(
            context: BuildContext,
            term: OrderedTupleTypeConstructorTerm,
        ): Stub<OrderedTupleTypeConstructor> {
            val elementStubs = term.elements.map {
                Element.build(
                    context,
                    element = it,
                )
            }

            return object : Stub<OrderedTupleTypeConstructor> {

                override val resolved: OrderedTupleTypeConstructor by lazy {
                    object : OrderedTupleTypeConstructor() {
                        override val outerScope: StaticScope = context.outerScope

                        override val term: OrderedTupleTypeConstructorTerm = term

                        override val elements: List<Element> by lazy {
                            elementStubs.map { it.resolved }
                        }
                    }
                }
            }
        }
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.traverseList(elements) { element ->
            element.classifiedElement
        }.transform { elements ->
            OrderedTupleType(
                elements = elements,
            ).asValue
        }
    }

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.traverseList(elements) { element ->
        element.type.bind(dynamicScope = dynamicScope).thenJust { elementType ->
            OrderedTupleType.Element(
                name = element.name, type = elementType.asType!!,
            )
        }
    }.thenJust { elements ->
        OrderedTupleType(elements = elements).asValue
    }

    override val subExpressions: Set<Expression>
        get() = elements.map { it.type }.toSet()
}

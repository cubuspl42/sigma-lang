package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.indexOfOrNull
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.IntLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.OrderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.OrderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Reference
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
    data class Element(
        override val name: Identifier?,
        override val typeThunk: Thunk<TypeAlike>,
    ) : TupleType.Entry() {
        constructor(
            name: Identifier?,
            type: TypeAlike,
        ) : this(
            name = name,
            typeThunk = Thunk.pure(type),
        )

        fun substituteTypeVariables(
            resolution: TypePlaceholderResolution,
        ): TypePlaceholderSubstitution<Element> = type.substituteTypePlaceholders(
            resolution = resolution,
        ).transform {
            copy(typeThunk = Thunk.pure(it))
        }
    }

    data class OrderedTupleMatch(
        val elementsMatches: List<SpecificType.MatchResult>,
        val sizeMatch: SizeMatchResult,
    ) : SpecificType.PartialMatch() {
        sealed class SizeMatchResult

        object SizeMatch : SizeMatchResult() {
            override fun toString(): String = "SizeMatch"
        }

        data class WrongSizeMismatch(
            val size: Int,
            val assignedSize: Int,
        ) : SizeMatchResult()

        override fun isFull(): Boolean = elementsMatches.all {
            it.isFull()
        } && sizeMatch is SizeMatch

        override fun dump(): String {
            if (sizeMatch is WrongSizeMismatch) {
                return "wrong size (required: ${sizeMatch.size}, actual: ${sizeMatch.assignedSize})"
            }

            return ArrayType.OrderedTupleMatch.dumpElementsMatches(
                elementsMatches = elementsMatches,
            )
        }
    }

    companion object {
        fun of(
            vararg elements: TypeAlike,
        ): OrderedTupleType = OrderedTupleType(
            elements = elements.map {
                Element(name = null, type = it)
            },
        )

        val Empty = OrderedTupleType(
            elements = emptyList(),
        )
    }

    override fun dumpDirectly(depth: Int): String {
        val dumpedEntries = elements.map { element ->
            listOfNotNull(
                element.name?.let { "${it.name}:" }, element.type.dumpRecursively(depth = depth + 1)
            ).joinToString(
                separator = " ",
            )
        }

        return "^[${dumpedEntries.joinToString(separator = ", ")}]"
    }

    override val keyType: PrimitiveType = IntCollectiveType

    override val valueType: SpecificType
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = elements.isEmpty()

    override fun resolveTypeVariablesShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is OrderedTupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-(ordered tuple) is assigned",
        )

        return elements.withIndex().fold(
            initial = TypePlaceholderResolution.Empty,
        ) { accumulatedResolution, (index, element) ->
            val assignedElement = assignedType.elements.getOrNull(index) ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple is shorter",
            )

            val elementResolution = element.type.resolveTypePlaceholders(
                assignedType = assignedElement.type as SpecificType,
            )

            accumulatedResolution.mergeWith(elementResolution)
        }
    }

    override fun matchShape(
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (assignedType) {
        is OrderedTupleType -> OrderedTupleMatch(
            elementsMatches = elements.zip(assignedType.elements) { element, assignedElement ->
                element.type.match(assignedType = assignedElement.type as SpecificType)
            },
            sizeMatch = when {
                assignedType.elements.size >= elements.size -> OrderedTupleMatch.SizeMatch
                else -> OrderedTupleMatch.WrongSizeMismatch(
                    size = elements.size,
                    assignedSize = assignedType.elements.size,
                )
            },
        )

        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override val entries: Collection<Entry> = elements

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.traverseIterable(elements) {
        it.substituteTypeVariables(resolution = resolution)
    }.transform { elements ->
        OrderedTupleType(elements = elements)
    }

    override val asArray: ArrayType by lazy {
        val elementType = elements.map { it.type }.fold(
            initial = NeverType as SpecificType,
        ) { accType, elementType ->
            accType.findLowestCommonSupertype(elementType as SpecificType)
        }

        ArrayType(
            elementType = elementType,
        )
    }

    override fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition> = elements.mapNotNull {
        val name = it.name
        val type = it.type

        // TODO: What with non-types??
        if (name != null && type is TypeType) {
            TypeVariableDefinition()
        } else null
    }.toSet()


//    override fun buildVariableExpressionDirectly(
//        context: VariableExpressionBuildingContext,
//    ): Expression = OrderedTupleConstructor(
//        elementsLazy = lazy {
//            elements.mapNotNull { element ->
//                (element.type as Type).buildVariableExpression(context = context)?.let { expression ->
//                    OrderedTupleConstructor.Element(
//                        name = element.name,
//                        typeLazy = lazy { expression },
//                    )
//                }
//            }
//        },
//    )

    override fun walkRecursive(): Sequence<SpecificType> = elements.asSequence().flatMap {
        (it.type as SpecificType).walk()
    }
}

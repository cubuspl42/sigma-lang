package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
    val indexedElements = elements.mapIndexed { index, element ->
        IndexedElement(
            key = IntValue(index.toLong()),
            name = element.name,
            typeThunk = element.typeThunk,
        )
    }

    constructor(
        elementsLazy: Lazy<List<Element>>,
    ) : this(
        elements = elementsLazy.value,
    )

    data class Element(
        val name: Identifier?,
        val typeThunk: Thunk<TypeAlike>,
    ) {
        constructor(
            name: Identifier?,
            type: TypeAlike,
        ) : this(
            name = name,
            typeThunk = Thunk.pure(type),
        )
    }

    data class IndexedElement(
        override val key: IntValue,
        override val name: Identifier?,
        override val typeThunk: Thunk<TypeAlike>,
    ) : TupleType.Entry() {
        fun substituteTypeVariables(
            resolution: TypePlaceholderResolution,
        ): TypePlaceholderSubstitution<Element> = type.substituteTypePlaceholders(
            resolution = resolution,
        ).transform {
            Element(
                name = name,
                typeThunk = Thunk.pure(it),
            )
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
        val dumpedEntries = indexedElements.map { element ->
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

    override fun resolveTypePlaceholdersShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is OrderedTupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-(ordered tuple) is assigned",
        )

        return indexedElements.withIndex().fold(
            initial = TypePlaceholderResolution.Empty,
        ) { accumulatedResolution, (index, element) ->
            val assignedElement = assignedType.indexedElements.getOrNull(index) ?: throw TypeVariableResolutionError(
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
            elementsMatches = indexedElements.zip(assignedType.indexedElements) { element, assignedElement ->
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

    override val entries: Collection<Entry> = indexedElements

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.traverseIterable(indexedElements) {
        it.substituteTypeVariables(resolution = resolution)
    }.transform { elements ->
        OrderedTupleType(elements = elements)
    }

    override fun replaceTypeRecursively(
        context: TypeReplacementContext,
    ): TypeAlike = OrderedTupleType(
        elementsLazy = lazy {
            this.elements.map { element ->
                element.copy(
                    typeThunk = element.typeThunk.thenJust {
                        val elementType = it as Type

                        elementType.replaceTypeDirectly(context = context)
                    }
                )
            }
        }
    )

    override val asArray: ArrayType by lazy {
        val elementType = indexedElements.map { it.type }.fold(
            initial = NeverType as SpecificType,
        ) { accType, elementType ->
            accType.findLowestCommonSupertype(elementType as SpecificType)
        }

        ArrayType(
            elementType = elementType,
        )
    }


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

    override fun getTypeByKey(key: PrimitiveValue): TypeAlike? = when (key) {
        is IntValue -> indexedElements.getOrNull(key.value.toInt())?.type
        is Identifier -> indexedElements.find { it.name == key }?.type
        else -> null
    }

    override fun walkRecursive(): Sequence<SpecificType> = indexedElements.asSequence().flatMap {
        (it.type as SpecificType).walk()
    }

    fun getIndexByName(name: Identifier): Int? = indexedElements.indexOfFirst { it.name == name }.takeIf { it >= 0 }
}

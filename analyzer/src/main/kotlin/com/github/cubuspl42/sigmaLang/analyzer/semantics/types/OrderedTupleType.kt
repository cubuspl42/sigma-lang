package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.indexOfOrNull
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
    data class Element(
        val name: Identifier?,
        val type: TypeAlike,
    ) {
        fun substituteTypeVariables(
            resolution: TypePlaceholderResolution,
        ): TypePlaceholderSubstitution<Element> = type.substituteTypePlaceholders(
            resolution = resolution,
        ).transform {
            copy(type = it)
        }

        fun toArgumentDeclaration(): AbstractionConstructor.ArgumentDeclaration? = name?.let {
            AbstractionConstructor.ArgumentDeclaration(
                name = it,
                annotatedType = type as MembershipType,
            )
        }
    }

    data class OrderedTupleMatch(
        val elementsMatches: List<MembershipType.MatchResult>,
        val sizeMatch: SizeMatchResult,
    ) : MembershipType.PartialMatch() {
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
        val dumpedEntries = elements.map { (name, type) ->
            listOfNotNull(
                name?.let { "${it.name}:" }, type.dumpRecursively(depth = depth + 1)
            ).joinToString(
                separator = " ",
            )
        }

        return "[${dumpedEntries.joinToString(separator = ", ")}]"
    }

    override val keyType: PrimitiveType = IntCollectiveType

    override val valueType: MembershipType
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
                assignedType = assignedElement.type as MembershipType,
            )

            accumulatedResolution.mergeWith(elementResolution)
        }
    }

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (assignedType) {
        is OrderedTupleType -> OrderedTupleMatch(
            elementsMatches = elements.zip(assignedType.elements) { element, assignedElement ->
                element.type.match(assignedType = assignedElement.type as MembershipType)
            },
            sizeMatch = when {
                assignedType.elements.size >= elements.size -> OrderedTupleMatch.SizeMatch
                else -> OrderedTupleMatch.WrongSizeMismatch(
                    size = elements.size,
                    assignedSize = assignedType.elements.size,
                )
            },
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock =
        AbstractionConstructor.ArgumentStaticBlock(
            argumentDeclarations = elements.mapNotNull { element ->
                element.toArgumentDeclaration()
            }.toSet(),
        )

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.traverseIterable(elements) {
        it.substituteTypeVariables(resolution = resolution)
    }.transform { elements ->
        OrderedTupleType(elements = elements)
    }

    override val asArray: ArrayType by lazy {
        val elementType = elements.map { it.type }.fold(
            initial = NeverType as MembershipType,
        ) { accType, elementType ->
            accType.findLowestCommonSupertype(elementType as MembershipType)
        }

        ArrayType(
            elementType = elementType,
        )
    }

    override fun toArgumentScope(argument: DictValue): DynamicScope = object : DynamicScope {
        override fun getValue(name: Declaration): Thunk<Value>? {
            val index = elements.indexOfOrNull { it.name == name.name } ?: return null

            return argument.read(IntValue(value = index.toLong()))
        }
    }

    override fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition> = elements.mapNotNull {
        val name = it.name
        val type = it.type

        // TODO: What with non-types??
        if (name != null && type is TypeType) {
            TypeVariableDefinition(
                name = name,
            )
        } else null
    }.toSet()

    override fun walkRecursive(): Sequence<MembershipType> = elements.asSequence().flatMap {
        (it.type as MembershipType).walk()
    }
}

package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.indexOfOrNull
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
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

    data class Element(
        // Idea: "label"?
        val name: Symbol?,
        val type: MembershipType,
    ) {
        fun substituteTypeVariables(
            resolution: TypeVariableResolution,
        ): Element = copy(
            type = type.substituteTypeVariables(
                resolution = resolution,
            )
        )

        fun toArgumentDeclaration(): AbstractionConstructor.ArgumentDeclaration? = name?.let {
            AbstractionConstructor.ArgumentDeclaration(
                name = it,
                annotatedType = type,
            )
        }
    }

    companion object {
        fun of(
            vararg elements: MembershipType,
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
        assignedType: MembershipType,
    ): TypeVariableResolution {
        if (assignedType !is OrderedTupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-(ordered tuple) is assigned",
        )

        return elements.withIndex().fold(
            initial = TypeVariableResolution.Empty,
        ) { accumulatedResolution, (index, element) ->
            val assignedElement = assignedType.elements.getOrNull(index) ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple is shorter",
            )

            val elementResolution = element.type.resolveTypeVariables(
                assignedType = assignedElement.type,
            )

            accumulatedResolution.mergeWith(elementResolution)
        }
    }

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (val sealedAssignedType = assignedType) {
        is OrderedTupleType -> OrderedTupleMatch(
            elementsMatches = elements.zip(sealedAssignedType.elements) { element, assignedElement ->
                element.type.match(assignedType = assignedElement.type)
            },
            sizeMatch = when {
                sealedAssignedType.elements.size >= elements.size -> OrderedTupleMatch.SizeMatch
                else -> OrderedTupleMatch.WrongSizeMismatch(
                    size = elements.size,
                    assignedSize = sealedAssignedType.elements.size,
                )
            },
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = sealedAssignedType,
        )
    }

    override fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock =
        AbstractionConstructor.ArgumentStaticBlock(
            argumentDeclarations = elements.mapNotNull { element ->
                element.toArgumentDeclaration()
            }.toSet(),
        )

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): OrderedTupleType = OrderedTupleType(
        elements = elements.map {
            it.substituteTypeVariables(resolution = resolution)
        },
    )

    override val asArray: ArrayType by lazy {
        val elementType = elements.map { it.type }.fold(
            initial = NeverType as MembershipType,
        ) { accType, elementType ->
            accType.findLowestCommonSupertype(elementType)
        }

        ArrayType(
            elementType = elementType,
        )
    }

    override fun toArgumentScope(argument: DictValue): DynamicScope = object : DynamicScope {
        override fun getValue(name: Symbol): Thunk<Value>? {
            val index = elements.indexOfOrNull { it.name == name } ?: return null

            return argument.read(IntValue(value = index.toLong()))?.toThunk()
        }
    }

    override val typeVariableDefinitions: Set<TypeVariableDefinition>
        get() = elements.mapNotNull {
            val name = it.name
            val type = it.type

            if (name != null && type is TypeType) {
                TypeVariableDefinition(
                    name = name,
                )
            } else null
        }.toSet()

    override fun walkRecursive(): Sequence<MembershipType> = elements.asSequence().flatMap {
        it.type.walk()
    }
}

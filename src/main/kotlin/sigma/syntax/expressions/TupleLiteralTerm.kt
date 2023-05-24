package sigma.syntax.expressions

import sigma.SyntaxValueScope
import sigma.TypeScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.values.tables.DictTable
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.syntax.SourceLocation

data class TupleLiteralTerm(
    override val location: SourceLocation,
    val associations: List<Association>,
) : ExpressionTerm() {
    sealed interface Association {
        val passedValue: ExpressionTerm

        val targetKey: PrimitiveValue
    }

    data class OrderedAssociation(
        val targetIndex: Int,
        override val passedValue: ExpressionTerm,
    ) : Association {
        override val targetKey: IntValue
            get() = IntValue(value = targetIndex.toLong())
    }

    data class UnorderedAssociation(
        val targetName: Symbol,
        override val passedValue: ExpressionTerm,
    ) : Association {
        override val targetKey: Symbol
            get() = targetName
    }

    // TODO: Share with `UnorderedTupleType`
    data class EntryType(
        val name: Symbol,
        val valueType: Type,
    )

    class DuplicatedNameError(
        duplicatedKey: PrimitiveValue,
    ) : TypeErrorException(
        message = "Duplicate key: ${duplicatedKey.dump()}",
    )

    companion object {
        fun build(
            ctx: SigmaParser.TupleLiteralContext,
        ): TupleLiteralTerm = TupleLiteralTerm(
            location = SourceLocation.build(ctx),
            associations = buildAssociations(
                nextTargetIndex = 0,
                remainingAssociations = ctx.tupleAssociation(),
            ),
        )

        private fun buildAssociations(
            nextTargetIndex: Int,
            remainingAssociations: List<SigmaParser.TupleAssociationContext>,
        ): List<Association> {
            val firstRemainingAssociation = remainingAssociations.firstOrNull() ?: return emptyList()
            val restRemainingAssociations = remainingAssociations.drop(1)

            val passedValue = ExpressionTerm.build(firstRemainingAssociation.passedValue)

            return when (val targetName = firstRemainingAssociation.targetName?.text) {
                null -> listOf(
                    OrderedAssociation(
                        targetIndex = nextTargetIndex,
                        passedValue = passedValue,
                    ),
                ) + buildAssociations(
                    nextTargetIndex = nextTargetIndex + 1,
                    remainingAssociations = restRemainingAssociations,
                )

                else -> listOf(
                    UnorderedAssociation(
                        targetName = Symbol.of(targetName),
                        passedValue = passedValue,
                    ),
                ) + buildAssociations(
                    nextTargetIndex = nextTargetIndex,
                    remainingAssociations = restRemainingAssociations,
                )
            }
        }
    }

    override fun dump(): String = "(dict constructor)"

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): TupleType {
        TODO()
    }

    override fun evaluate(
        scope: Scope,
    ): DictTable = DictTable(
        entries = associations.associate {
            it.targetKey to it.passedValue.bind(scope = scope)
        },
    )
}

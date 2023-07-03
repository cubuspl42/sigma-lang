package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.UnorderedTupleConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.Scope
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException

data class UnorderedTupleConstructorTerm(
    override val location: SourceLocation,
    val entries: List<Entry>,
) : TupleConstructorTerm() {
    data class Entry(
        val name: Symbol,
        val value: ExpressionTerm,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.UnorderedTupleAssociationContext,
            ): Entry = Entry(
                name = Symbol.of(ctx.name.text),
                value = ExpressionTerm.build(ctx.value),
            )
        }
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
            ctx: UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            location = SourceLocation.build(ctx),
            entries = ctx.unorderedTupleAssociation().map {
                Entry.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"

    override fun evaluate(
        scope: Scope,
    ): DictTable = DictTable(
        entries = entries.associate {
            it.name to it.value.bind(scope = scope)
        },
    )
}

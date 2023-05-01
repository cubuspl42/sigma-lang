package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.semantics.types.Type
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.values.Symbol
import sigma.values.TypeErrorException
import sigma.values.tables.DictTable
import sigma.values.tables.Scope

data class FieldReadTerm(
    override val location: SourceLocation,
    val subject: ExpressionTerm,
    val fieldName: Symbol,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: FieldReadAltContext,
        ): FieldReadTerm = FieldReadTerm(
            location = SourceLocation.build(ctx),
            subject = build(ctx.subject),
            fieldName = Symbol.of(ctx.fieldName.text),
        )
    }

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type {
        val subjectType = subject.determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        ) as? UnorderedTupleType ?: throw TypeErrorException(
            location = location,
            message = "Fields can be read only from unordered tuples",
        )

        val fieldType = subjectType.getFieldType(key = fieldName) ?: throw TypeErrorException(
            location = location,
            message = "Key ${fieldName.dump()} is missing ing ${subjectType.dump()}",
        )

        return fieldType
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val subjectValue = subject.evaluate(scope = scope).toEvaluatedValue

        if (subjectValue !is DictTable) throw IllegalStateException("Subject $subjectValue is not a dict")

        val value = subjectValue.apply(fieldName)

        return value
    }

    override fun dump(): String = "${subject.dump()}.${fieldName.name}"
}
package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.semantics.types.Type
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.DictTable
import sigma.values.tables.Scope

data class FieldRead(
    override val location: SourceLocation,
    val subject: Expression,
    val fieldName: Symbol,
) : Expression() {
    companion object {
        fun build(
            ctx: FieldReadAltContext,
        ): FieldRead = FieldRead(
            location = SourceLocation.build(ctx),
            subject = build(ctx.subject),
            fieldName = Symbol.of(ctx.fieldName.text),
        )
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val subjectType = subject.determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        ) as? UnorderedTupleType ?: throw TypeError(
            location = location,
            message = "Fields can be read only from unordered tuples",
        )

        val fieldType = subjectType.getFieldType(key = fieldName) ?: throw TypeError(
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

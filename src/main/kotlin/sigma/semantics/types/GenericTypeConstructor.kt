package sigma.semantics.types

import sigma.semantics.DeclarationScope
import sigma.syntax.expressions.GenericParametersTuple
import sigma.syntax.typeExpressions.TypeExpressionTerm

class GenericTypeConstructor(
    val context: DeclarationScope,
    val argumentMetaType: GenericParametersTuple,
    val bodyTerm: TypeExpressionTerm,
    val body: TypeEntity,
) : TypeEntity() {
    fun call(passedArgument: OrderedTypeTuple): TypeEntity {
        // TODO
        return bodyTerm.evaluate(declarationScope = context)
    }
}

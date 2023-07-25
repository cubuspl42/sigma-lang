package sigma.semantics.types

import sigma.syntax.typeExpressions.TypeExpressionTerm

class GenericTypeConstructor(
    val body: TypeExpressionTerm,
) : TypeEntity()

package sigma

import sigma.expressions.Expression

class DictAssociativeTable(
    override val environment: Table,
    associations: Map<Value, Expression>,
) : AssociativeTable(associations = associations)

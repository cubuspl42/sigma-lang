package sigma.values.tables

import sigma.expressions.Expression
import sigma.values.Value

class DictAssociativeTable(
    override val environment: Table,
    associations: Map<Value, Expression>,
) : AssociativeTable(associations = associations)

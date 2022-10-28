package sigma

class DictAssociativeTable(
    override val environment: Table,
    associations: Map<Value, Expression>,
) : AssociativeTable(associations = associations)

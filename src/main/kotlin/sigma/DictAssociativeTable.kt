package sigma

class DictAssociativeTable(
    override val environment: Table,
    associations: ExpressionTable,
) : AssociativeTable(associations = associations)

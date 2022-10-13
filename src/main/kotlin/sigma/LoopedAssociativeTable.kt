package sigma

class LoopedAssociativeTable(
    context: Table,
    associations: ExpressionTable,
) : AssociativeTable(associations = associations) {
    override val environment: Table = this.chainWith(context = context)
}

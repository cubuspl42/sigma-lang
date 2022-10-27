package sigma

data class TypeReference(
    val referee: Symbol,
) : TypeExpression {
    override fun evaluate(
        context: TypeContext,
    ): Type? = context.getType(
        name = referee,
    )
}

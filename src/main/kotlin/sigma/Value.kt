package sigma

sealed class Value : Expression {
    override fun evaluate(scope: Scope): Value = this
}

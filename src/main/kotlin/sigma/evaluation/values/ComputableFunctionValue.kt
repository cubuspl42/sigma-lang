package sigma.evaluation.values

// Thought: Is this needed anymore?
abstract class ComputableFunctionValue : FunctionValue() {
    override fun equalsTo(other: Value): Boolean = this == other

    override fun dump(): String = "(computable function)"
}

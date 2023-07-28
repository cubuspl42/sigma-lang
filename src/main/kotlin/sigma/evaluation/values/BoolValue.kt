package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

data class BoolValue(
    val value: Boolean,
) : PrimitiveValue() {
    companion object {
        val False = BoolValue(false)

        val True = BoolValue(true)
    }

    object If : ComputableFunctionValue() {
        override fun apply(context: EvaluationContext, argument: Value): ValueResult {
            val test = (argument as DictValue).read(IntValue.Zero)!! as BoolValue

            return object : ComputableFunctionValue() {
                override fun apply(context: EvaluationContext, argument: Value): EvaluationResult {
                    val branches = argument as FunctionValue

                    return when {
                        test.value -> branches.apply(context, Symbol.of("then"))
                        else -> branches.apply(context, Symbol.of("else"))
                    }
                }

                override fun dump(): String = "(if')"
            }.asEvaluationResult
        }

        override fun dump(): String = "(if)"
    }

    override fun dump(): String = value.toString()
}

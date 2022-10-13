package sigma

abstract class ComputableFunctionValue : FunctionValue() {
    final override fun isSame(other: Value): Boolean {
        throw UnsupportedOperationException()
    }
}

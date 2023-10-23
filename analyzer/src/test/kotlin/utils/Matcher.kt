package utils

abstract class Matcher<in T> {
    open val description: String? = null

    class Equals<T>(
        private val expected: T,
    ) : Matcher<T>() {
        override fun match(actual: T) {
            if (actual != expected) {
                throw AssertionError("Expected $expected, but got $actual")
            }
        }
    }

    class Irrelevant<T> : Matcher<T>() {
        override fun match(actual: T) {
        }
    }

    companion object {
        @Suppress("TestFunctionName")
        fun <T> IsNull(): Matcher<T?> = object : Matcher<Any?>() {
            override fun match(actual: Any?) {
                if (actual != null) {
                    throw AssertionError("Expected null, but got $actual")
                }
            }
        }

        @Suppress("TestFunctionName")
        inline fun <reified T> Is(
            matcher: Matcher<T>? = null,
        ): Matcher<Any?> = object : Matcher<Any?>() {
            override fun match(actual: Any?) {
                if (actual !is T) {
                    val actualName = actual?.let { it::class.simpleName } ?: "null"

                    throw AssertionError("Expected ${T::class.simpleName}, but got $actualName")
                }

                matcher?.match(actual = actual)
            }
        }
    }

    abstract fun match(actual: T)

}

fun <T> Matcher<T>.with(other: Matcher<T>): Matcher<T> = object : Matcher<T>() {
    override fun match(actual: T) {
        this@with.match(actual)
        other.match(actual)
    }
}

inline fun <B : Any, reified T : B> Matcher<T>.checked(): Matcher<B> = object : Matcher<B>() {
    override fun match(actual: B) {
        if (actual is T) {
            return this@checked.match(actual)
        } else {
            throw AssertionError("Expected ${T::class.simpleName}, but got ${actual::class.simpleName}")
        }
    }
}

fun <T> Iterable<Matcher<T>>.match(actual: Iterable<T>) {
    val expectedIterator = this.iterator()
    val actualIterator = actual.iterator()

    while (expectedIterator.hasNext() && actualIterator.hasNext()) {
        expectedIterator.next().match(actual = actualIterator.next())
    }

    if (expectedIterator.hasNext()) {
        throw AssertionError("Expected more elements")
    }

    if (actualIterator.hasNext()) {
        throw AssertionError("Expected less elements")
    }
}

fun <T> assertMatches(
    matcher: Matcher<T>,
    actual: T,
) {
    matcher.match(actual = actual)
}

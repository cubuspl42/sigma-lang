package sigma

abstract class Computation<out A> {
    sealed interface Result<out A> {
        data class Computed<A>(
            val value: A,
        ) : Result<A>

        class CyclicError<A> : Result<A>
    }

    class Context(
        private val inProgress: Set<Computation<*>>,
    ) {
        fun isStarted(
            computation: Computation<*>,
        ): Boolean = inProgress.contains(computation)

        fun withStarted(
            computation: Computation<*>,
        ): Context = Context(
            inProgress = inProgress + computation,
        )

        companion object {
            val Empty: Computation.Context = Context(inProgress = emptySet())
        }
    }

    companion object {
        fun <A : Any> lazy(get: () -> Computation<A>): Computation<A> = object : Computation<A>() {
            private val computation by kotlin.lazy { get() }

            override fun computeDirectly(innerContext: Context): Result<A> =
                computation.compute(outerContext = innerContext)
        }

        fun <A : Any> pure(
            value: A,
        ): Computation<A> = object : Computation<A>() {
            override fun computeDirectly(
                innerContext: Context,
            ): Result<A> = Result.Computed(value = value)
        }

        fun <A, B, C> combine2(
            computation1: Computation<A>,
            computation2: Computation<B>,
            combine: (A, B) -> C,
        ): Computation<C> = object : Computation<C>() {
            override fun computeDirectly(innerContext: Context): Result<C> {
                val value1 = when (val it = computation1.compute(outerContext = innerContext)) {
                    is Result.Computed -> it.value
                    is Result.CyclicError -> return Result.CyclicError()
                }

                val value2 = when (val it = computation2.compute(outerContext = innerContext)) {
                    is Result.Computed -> it.value
                    is Result.CyclicError -> return Result.CyclicError()
                }

                return Result.Computed(
                    value = combine(value1, value2)
                )
            }
        }

        fun <A : Any, B : Any> traverseList(
            list: List<A>,
            transform: (A) -> Computation<B>,
        ): Computation<List<B>> = object : Computation<List<B>>() {
            override fun computeDirectly(innerContext: Context): Result<List<B>> {
                val computations = list.map(transform)

                val results = computations.map {
                    it.compute(outerContext = innerContext)
                }

                if (results.any { it is Result.CyclicError }) {
                    return Result.CyclicError()
                }

                return Result.Computed(
                    value = results.map { (it as Result.Computed<B>).value }
                )
            }
        }
    }

    val result: Result<A> by kotlin.lazy {
        compute(outerContext = Context.Empty)
    }

    val value: A? by kotlin.lazy {
        (result as? Result.Computed<A>)?.value
    }

    fun compute(
        outerContext: Context,
    ): Result<A> = if (::cachedResult.isInitialized) {
        cachedResult
    } else {
        val result = if (outerContext.isStarted(this)) {
            Result.CyclicError()
        } else {
            computeDirectly(
                innerContext = outerContext.withStarted(this)
            )
        }

        cachedResult = result

        result
    }

    private lateinit var cachedResult: Result<A>

    protected abstract fun computeDirectly(innerContext: Context): Result<A>

    fun <B> thenJust(
        transform: (A) -> B,
    ): Computation<B> = object : Computation<B>() {
        override fun computeDirectly(innerContext: Context): Result<B> {
            val value = when (val it = this@Computation.compute(outerContext = innerContext)) {
                is Result.Computed -> it.value
                is Result.CyclicError -> return Result.CyclicError()
            }

            return Result.Computed(
                value = transform(value)
            )
        }
    }

    fun <B : Any> thenDo(
        transform: (A) -> Computation<B>,
    ): Computation<B> = object : Computation<B>() {
        override fun computeDirectly(innerContext: Context): Result<B> {
            val value = when (val it = this@Computation.compute(outerContext = innerContext)) {
                is Result.Computed -> it.value
                is Result.CyclicError -> return Result.CyclicError()
            }

            return transform(value).compute(outerContext = innerContext)
        }
    }
}


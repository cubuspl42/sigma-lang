package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.EvaluationContext

abstract class Thunk<out ResultType> {
    companion object {
        fun <A> flatten(thunk: Thunk<Thunk<A>>): Thunk<A> = object : Thunk<A>() {
            override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<A> {
                val innerThunk = when (val outcome = thunk.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                return innerThunk.evaluate(context = context)
            }
        }

        fun <A : Any> lazy(get: () -> Thunk<A>): Thunk<A> = object : Thunk<A>() {
            private val computation by kotlin.lazy { get() }

            override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<A> =
                computation.evaluate(context = context)
        }

        fun <A> pure(
            value: A,
        ): Thunk<A> = object : Thunk<A>() {
            override fun evaluateDirectly(
                context: EvaluationContext,
            ): EvaluationOutcome<A> = EvaluationResult(value = value)
        }

        fun <A, B, C> combine2(
            thunk1: Thunk<A>,
            thunk2: Thunk<B>,
            combine: (A, B) -> C,
        ): Thunk<C> = object : Thunk<C>() {
            override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<C> {
                val value1 = when (val outcome = thunk1.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                val value2 = when (val outcome = thunk2.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                return EvaluationResult(
                    value = combine(value1, value2)
                )
            }
        }

        fun <A, B, C, D> combine3(
            thunk1: Thunk<A>,
            thunk2: Thunk<B>,
            thunk3: Thunk<C>,
            combine: (A, B, C) -> D,
        ): Thunk<D> = object : Thunk<D>() {
            override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<D> {
                val value1 = when (val outcome = thunk1.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                val value2 = when (val outcome = thunk2.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                val value3 = when (val outcome = thunk3.evaluate(context = context)) {
                    is EvaluationResult -> outcome.value
                    is EvaluationError -> return outcome
                }

                return EvaluationResult(
                    value = combine(value1, value2, value3)
                )
            }
        }

        fun <A : Any, B : Any> traverseList(
            list: List<A>,
            transform: (A) -> Thunk<B>,
        ): Thunk<List<B>> = object : Thunk<List<B>>() {
            override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<List<B>> {
                val computations = list.map(transform)

                val results = computations.map {
                    it.evaluate(context = context)
                }

                val error = results.firstNotNullOfOrNull { it as? EvaluationError }

                if (error != null) return error

                return EvaluationResult(value = results.map { (it as EvaluationResult<B>).value })
            }
        }
    }

    private lateinit var cachedResult: EvaluationOutcome<ResultType>

    fun evaluate(
        context: EvaluationContext,
    ): EvaluationOutcome<ResultType> {
        val innerContext = context.withIncreasedDepth()

        return when {
            innerContext.evaluationDepth > EvaluationContext.maxEvaluationDepth -> EvaluationStackExhaustionError

            this::cachedResult.isInitialized -> {
                this.cachedResult
            }

            else -> {
                val result = this.evaluateDirectly(
                    context = innerContext,
                )

                this.cachedResult = result

                result
            }
        }
    }

    abstract fun evaluateDirectly(
        context: EvaluationContext,
    ): EvaluationOutcome<ResultType>

    fun evaluateInitial(): EvaluationOutcome<ResultType> = evaluate(
        context = EvaluationContext.Initial,
    )

    fun <B> thenJust(
        transform: (ResultType) -> B,
    ): Thunk<B> = object : Thunk<B>() {
        override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<B> {
            val value = when (val outcome = this@Thunk.evaluate(context = context)) {
                is EvaluationResult -> outcome.value
                is EvaluationError -> return outcome
            }

            return EvaluationResult(
                value = transform(value)
            )
        }
    }

    fun <B> thenDo(
        transform: (ResultType) -> Thunk<B>,
    ): Thunk<B> = object : Thunk<B>() {
        override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<B> {
            val value = when (val outcome = this@Thunk.evaluate(context = context)) {
                is EvaluationResult -> outcome.value
                is EvaluationError -> return outcome
            }

            return transform(value).evaluate(context = context)
        }
    }

    val outcome: EvaluationOutcome<ResultType> by kotlin.lazy {
        evaluate(context = EvaluationContext.Initial)
    }

    val value: ResultType? by kotlin.lazy {
        (outcome as? EvaluationResult<ResultType>)?.value
    }
}

// Nuke?
fun <ResultType> ResultType.toEvaluationResult(): EvaluationResult<ResultType> = EvaluationResult(value = this)

fun <ResultType> ResultType.toThunk(): Thunk<ResultType> = object : Thunk<ResultType>() {
    override fun evaluateDirectly(
        context: EvaluationContext,
    ): EvaluationOutcome<ResultType> = this@toThunk.toEvaluationResult()
}

fun Thunk<Value>.evaluateInitialValue(): Value = (evaluateInitial() as EvaluationResult).value

fun Thunk<Value>.evaluateValueHacky(
    context: EvaluationContext,
): Value? = (evaluate(
    context = context,
) as? EvaluationResult<Value>)?.value

abstract class CachingThunk<ResultType : Any> : Thunk<ResultType>() {
    private lateinit var cachedResult: EvaluationOutcome<ResultType>
    override fun evaluateDirectly(
        context: EvaluationContext,
    ): EvaluationOutcome<ResultType> = if (this::cachedResult.isInitialized) {
        this.cachedResult
    } else {
        val result = this.evaluate(
            context = context,
        )

        this.cachedResult = result

        result
    }
}

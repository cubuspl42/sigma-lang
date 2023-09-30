package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.cutOffHead
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction

sealed class ClassificationContext<out A> {
    abstract val referredDeclarations: Set<Introduction>

    abstract fun bind(dynamicScope: DynamicScope): Thunk<A>

    fun <B> transform(transform: (A) -> B): ClassificationContext<B> = transformThen { Thunk.pure(transform(it)) }

    fun <B> transformThen(transform: (A) -> Thunk<B>): ClassificationContext<B> = transformThunk { thunk ->
        thunk.thenDo(transform)
    }

    abstract fun <B> transformThunk(transform: (Thunk<A>) -> Thunk<B>): ClassificationContext<B>


    companion object {
        fun <A, B, C> transformThunk2(
            context1: ClassificationContext<A>,
            context2: ClassificationContext<B>,
            combine: (Thunk<A>, Thunk<B>) -> Thunk<C>,
        ): ClassificationContext<C> = when {
            context1 is ConstClassificationContext && context2 is ConstClassificationContext -> {
                object : ConstClassificationContext<C>() {
                    override val valueThunk: Thunk<C> = combine(
                        context1.valueThunk,
                        context2.valueThunk,
                    )
                }
            }

            else -> {
                object : VariableClassificationContext<C>() {
                    override val referredDeclarations: Set<Introduction> =
                        context1.referredDeclarations + context2.referredDeclarations

                    override fun bind(dynamicScope: DynamicScope): Thunk<C> = combine(
                        context1.bind(dynamicScope = dynamicScope),
                        context2.bind(dynamicScope = dynamicScope),
                    )
                }
            }
        }

        fun <A, B, C> transform2(
            context1: ClassificationContext<A>,
            context2: ClassificationContext<B>,
            combine: (A, B) -> Thunk<C>,
        ): ClassificationContext<C> = transformThunk2(
            context1 = context1,
            context2 = context2,
        ) { thunk1, thunk2 ->
            Thunk.combine2(
                thunk1 = thunk1,
                thunk2 = thunk2,
                combine = combine,
            ).thenDo { it }
        }

        fun <A, B, C, D> transform3(
            context1: ClassificationContext<A>,
            context2: ClassificationContext<B>,
            context3: ClassificationContext<C>,
            combine: (A, B, C) -> Thunk<D>,
        ): ClassificationContext<D> = when {
            context1 is ConstClassificationContext && context2 is ConstClassificationContext && context3 is ConstClassificationContext -> {
                object : ConstClassificationContext<D>() {
                    override val valueThunk: Thunk<D> = Thunk.combine3(
                        thunk1 = context1.valueThunk,
                        thunk2 = context2.valueThunk,
                        thunk3 = context3.valueThunk,
                        combine = combine,
                    ).thenDo { it }
                }
            }

            else -> {
                object : VariableClassificationContext<D>() {
                    override val referredDeclarations: Set<Introduction> =
                        context1.referredDeclarations + context2.referredDeclarations + context3.referredDeclarations

                    override fun bind(dynamicScope: DynamicScope): Thunk<D> = Thunk.combine3(
                        thunk1 = context1.bind(dynamicScope = dynamicScope),
                        thunk2 = context2.bind(dynamicScope = dynamicScope),
                        thunk3 = context3.bind(dynamicScope = dynamicScope),
                        combine = combine,
                    ).thenDo { it }
                }
            }
        }

        fun <A, B> traverseListThunk(
            list: List<A>,
            extract: (A) -> ClassificationContext<B>,
        ): ClassificationContext<List<B>> {
            val (head, tail) = list.cutOffHead() ?: return ConstClassificationContext.pure(emptyList())

            return transformThunk2(
                context1 = extract(head),
                context2 = traverseList(tail, extract = extract),
            ) { newHead, newTail ->
                Thunk.combine2(
                    thunk1 = newHead,
                    thunk2 = newTail,
                    combine = { head, tail ->
                        listOf(head) + tail
                    },
                )
            }
        }

        fun <A, B> traverseList(
            list: List<A>,
            extract: (A) -> ClassificationContext<B>,
        ): ClassificationContext<List<B>> {
            val (head, tail) = list.cutOffHead() ?: return ConstClassificationContext.pure(emptyList())

            return transform2(
                context1 = extract(head),
                context2 = traverseList(tail, extract = extract),
            ) { newHead, newTail ->
                Thunk.pure(listOf(newHead) + newTail)
            }
        }
    }
}

fun <A, B> ClassificationContext<Thunk<A>>.transformNested(
    transform: (A) -> B,
): ClassificationContext<Thunk<B>> = transform { thunk ->
    thunk.thenJust(transform)
}

abstract class ConstClassificationContext<A> : ClassificationContext<A>() {
    companion object {
        fun <A> pure(value: A): ClassificationContext<A> = object : ConstClassificationContext<A>() {
            override val valueThunk: Thunk<A> = Thunk.pure(value)
        }
    }

    final override val referredDeclarations: Set<Introduction> = emptySet()

    final override fun <B> transformThunk(transform: (Thunk<A>) -> Thunk<B>): ConstClassificationContext<B> =
        object : ConstClassificationContext<B>() {
            override val valueThunk: Thunk<B> = transform(this@ConstClassificationContext.valueThunk)
        }

    override fun bind(dynamicScope: DynamicScope): Thunk<A> = valueThunk

    abstract val valueThunk: Thunk<A>
}

abstract class VariableClassificationContext<A> : ClassificationContext<A>() {
    final override fun <B> transformThunk(transform: (Thunk<A>) -> Thunk<B>): VariableClassificationContext<B> =
        object : VariableClassificationContext<B>() {
            override val referredDeclarations: Set<Introduction>
                get() = this@VariableClassificationContext.referredDeclarations

            override fun bind(dynamicScope: DynamicScope): Thunk<B> =
                transform(this@VariableClassificationContext.bind(dynamicScope = dynamicScope))
        }

    fun withResolvedDeclarations(
        declarations: Set<Introduction>,
        buildConst: () -> Thunk<Value>,
        buildVariable: (dynamicScope: DynamicScope) -> Thunk<Value>,
    ): ClassificationContext<Value> {
        val remainingReferredDeclarations = referredDeclarations - declarations

        return if (remainingReferredDeclarations.isEmpty()) {
            object : ConstClassificationContext<Value>() {
                override val valueThunk: Thunk<Value> = buildConst()
            }
        } else {
            object : VariableClassificationContext<Value>() {
                override val referredDeclarations: Set<Introduction> = remainingReferredDeclarations

                override fun bind(dynamicScope: DynamicScope): Thunk<Value> = buildVariable(dynamicScope)
            }
        }
    }
}

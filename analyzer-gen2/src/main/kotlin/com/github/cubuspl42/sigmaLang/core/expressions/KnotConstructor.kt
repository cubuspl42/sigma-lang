package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.concepts.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.concepts.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.withValue
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazy
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

abstract class Wrapper : ComplexExpression()

class KnotConstructor private constructor(
    private val body: Expression,
) : Wrapper() {
    abstract class CodegenRepresentation : Expression.CodegenRepresentation() {
        abstract val knotName: String

        abstract val result: Expression.CodegenRepresentation

        final override fun generateCode(): CodeBlock {
            val objectBuilder = TypeSpec.anonymousClassBuilder().addProperty(
                PropertySpec.builder(
                    name = knotName,
                    type = Module.CodegenRepresentationContext.valueTypeName,
                ).delegate(
                    result.generateCode().wrapWithLazy(),
                ).build(),
            )

            return CodeBlock.of(
                "%L.${knotName}",
                objectBuilder.build(),
            )
        }
    }

    companion object {
        fun of(
            buildBody: (KnotReference) -> Expression,
        ): KnotConstructor = LazyUtils.looped { knotConstructorLooped ->
            val reference = KnotReference(
                referredKnotLazy = knotConstructorLooped,
            )

            val body = buildBody(reference)

            KnotConstructor(
                body = body,
            )
        }

        fun <T> looped(
            buildBody: (KnotReference) -> Pair<Expression, T>,
        ): Pair<KnotConstructor, T> = LazyUtils.looped2 { knotConstructorLooped, _ ->
            val reference = KnotReference(
                referredKnotLazy = knotConstructorLooped,
            )

            val (body, t) = buildBody(reference)

            val knotConstructor = KnotConstructor(
                body = body,
            )

            Pair(
                knotConstructor,
                t,
            )
        }

        fun <TExpression : ShadowExpression> builder(
            buildBody: (KnotReference) -> ExpressionBuilder<TExpression>,
        ): ExpressionBuilder<TExpression> = object : ExpressionBuilder<TExpression>() {
            override fun build(buildContext: BuildContext): TExpression {
                val (_, body) = LazyUtils.looped2 { knotConstructorLooped, _ ->
                    val reference = KnotReference(
                        referredKnotLazy = knotConstructorLooped,
                    )

                    val bodyBuilder = buildBody(reference)

                    val body = bodyBuilder.build(buildContext = buildContext)

                    val knotConstructor = KnotConstructor(
                        body = body.rawExpression,
                    )

                    Pair(
                        knotConstructor,
                        body,
                    )
                }

                return body
            }
        }
    }

    override val subExpressions: Set<Expression> by lazy {
        setOf(body)
    }

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): Expression.CodegenRepresentation = object : CodegenRepresentation() {
        override val knotName: String = context.generateUniqueName(prefix = "knot")

        override val result: Expression.CodegenRepresentation by lazy {
            context.getRepresentation(this@KnotConstructor.body)
        }
    }

    override fun bind(
        scope: DynamicScope,
    ): Lazy<Value> = lazyOf(
        DynamicScope.looped { innerScopeLooped ->
            val body = this@KnotConstructor.body.bind(
                scope = innerScopeLooped,
            ).value

            val innerScope = scope.withValue(
                wrapper = this@KnotConstructor,
                value = body,
            )

            Pair(body, innerScope)
        },
    )
}

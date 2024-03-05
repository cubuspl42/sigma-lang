package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.withValue
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

abstract class Wrapper : ComplexExpression()

class KnotConstructor private constructor(
    private val body: Expression,
) : Wrapper() {
    class DefinitionCodegenRepresentation(
        val name: String,
        val initializer: Expression.CodegenRepresentation,
    ) {
        fun generateDefinition(): PropertySpec = PropertySpec.builder(
            name = name,
            type = Module.CodegenRepresentationContext.lazyValueTypeName,
        ).initializer(
            initializer.generateCode()
        ).build()

        fun generateLazierReference(): CodeBlock = CodeBlock.of(
            """
                %T.lazier { $name }
            """.trimIndent(),
            LazyUtils::class,
        )
    }

    abstract class CodegenRepresentation : Expression.CodegenRepresentation() {
        abstract val knotName: String

        abstract val localProperties: List<DefinitionCodegenRepresentation>

        abstract val result: Expression.CodegenRepresentation

        final override fun generateCode(): CodeBlock {
            val objectBuilder = TypeSpec.anonymousClassBuilder().addProperty(
                PropertySpec.builder(
                    name = knotName,
                    type = Module.CodegenRepresentationContext.lazyValueTypeName,
                ).initializer(
                    UnorderedTupleConstructor.generateCode(
                        valueByKey = localProperties.associate {
                            Identifier(name = it.name) to it.generateLazierReference()
                        },
                    ).wrapWithLazyOf(),
                ).build(),
            )

            localProperties.forEach {
                objectBuilder.addProperty(it.generateDefinition())
            }

            objectBuilder.addProperty(
                PropertySpec.builder(
                    name = "result",
                    type = Module.CodegenRepresentationContext.lazyValueTypeName,
                ).initializer(result.generateCode()).build()
            )

            return CodeBlock.of("%L.result", objectBuilder.build())
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
    }

    override val subExpressions: Set<Expression> by lazy {
        setOf(body)
    }

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): Expression.CodegenRepresentation = TODO()

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

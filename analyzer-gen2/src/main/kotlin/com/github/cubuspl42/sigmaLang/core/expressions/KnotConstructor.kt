package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class KnotConstructor(
    private val definitionByIdentifier: Map<Identifier, Lazy<Expression>>,
    private val resultLazy: Lazy<Expression>,
) : Expression() {

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

    private val result by resultLazy

    override val subExpressions: Set<Expression> by lazy {
        definitionByIdentifier.values.map { it.value }.toSet() + result
    }

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): Expression.CodegenRepresentation = object : CodegenRepresentation() {
        override val knotName: String = context.generateUniqueName(prefix = "knot")

        override val localProperties: List<DefinitionCodegenRepresentation> by lazy {
            definitionByIdentifier.mapNotNull { (name, initializer) ->
                DefinitionCodegenRepresentation(
                    name = name.name,
                    initializer = context.getRepresentation(initializer.value),
                )
            }
        }

        override val result: Expression.CodegenRepresentation by lazy {
            context.getRepresentation(this@KnotConstructor.result)
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = object {
        val innerScope: DynamicScope by lazy {
            scope.withWrappingKnot(
                knotConstructor = this@KnotConstructor, value = UnorderedTuple(
                    valueByKey = definitionByIdentifier.mapValues { (_, expression) ->
                        lazy { expression.value.bind(scope = innerScope).value }
                    },
                )
            )
        }

        val result = this@KnotConstructor.result.bind(
            scope = innerScope,
        )
    }.result

    fun getDefinition(
        identifier: Identifier,
    ): Lazy<Expression>? = definitionByIdentifier[identifier]
}

package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import com.github.cubuspl42.sigmaLang.utils.mapUniquely
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec


class ProjectBuilder(
    private val moduleDefinitionBuilders: Set<ModuleDefinitionBuilder>,
) {
    data class ModuleDefinitionBuilder(
        private val name: Identifier,
        private val initializer: ExpressionBuilder<Expression>,
    ) {
        fun build(
            buildContext: Expression.BuildContext,
        ) = UnorderedTupleConstructor.Entry(
            key = name,
            value = lazy {
                initializer.build(
                    buildContext = buildContext,
                )
            },
        )
    }

    data class Constructor(
        val rootKnotConstructor: KnotConstructor,
    ) : ShadowExpression() {
        override val rawExpression: Expression
            get() = rootKnotConstructor

        fun generateCode(packageName: String, name: String): FileSpec {
            val context = CodegenRepresentationContext().apply {
                visitOnce(rawExpression)
            }

            val rootRepresentation = context.getRepresentation(rootKnotConstructor)

            return FileSpec.builder(packageName, "out").addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("%S", "RedundantVisibilityModifier")
                    .addMember("%S", "unused").build()
            ).addType(
                TypeSpec.objectBuilder(
                    name = name,
                ).addProperty(
                    PropertySpec.builder(
                        name = "root",
                        type = CodegenRepresentationContext.valueTypeName,
                    ).initializer(
                        rootRepresentation.generateCode(),
                    ).build()
                ).build()
            ).build()
        }

        fun evaluate(): Value = LazyUtils.looped { rootLooped ->
            rawExpression.bindStrict(
                context = DynamicContext(
                    rootLazy = rootLooped,
                    scope = DynamicScope.Bottom,
                ),
            )
        }
    }

    data class Reference(
        val rawKnotReference: Expression,
    ) {
        fun resolveModule(modulePath: ModulePath): Expression = rawKnotReference.readField(
            fieldName = modulePath.name,
        )
    }

    companion object {
        val builtinIdentifier = Identifier(name = "__builtin__")
    }

    fun build(): Constructor {
        val (
            rootKnotConstructor,
            _,
        ) = KnotConstructor.looped { knotReference ->
            val projectReference = Reference(
                rawKnotReference = knotReference,
            )

            val moduleEntries = moduleDefinitionBuilders.mapUniquely {
                it.build(
                    buildContext = Expression.BuildContext(
                        projectReference = projectReference,
                    )
                )
            }

            val rootTupleConstructor = UnorderedTupleConstructor.fromEntries(
                moduleEntries + UnorderedTupleConstructor.Entry(
                    key = builtinIdentifier,
                    value = lazy { BuiltinModuleConstructor },
                ),
            )

            Pair(
                rootTupleConstructor,
                Unit,
            )
        }

        return Constructor(
            rootKnotConstructor = rootKnotConstructor,
        )
    }
}

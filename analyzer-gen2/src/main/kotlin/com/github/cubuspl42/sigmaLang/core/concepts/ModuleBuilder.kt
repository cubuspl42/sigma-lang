package com.github.cubuspl42.sigmaLang.core.concepts

import com.github.cubuspl42.sigmaLang.BuiltinScope
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.concepts.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.ExpressedAbstraction
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm
import com.github.cubuspl42.sigmaLang.utils.mapUniquely
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class ModuleBuilder(
    private val memberDefinitionBuilders: Set<MemberDefinitionBuilder>,
) {
    interface MostInnerPartialConstructor {
        val memberDefinitions: Set<Constructor.MemberDefinition>
    }

    interface InnerPartialConstructor : MostInnerPartialConstructor {
        val memberKnotConstructor: KnotConstructor
    }

    data class Constructor(
        val rootAbstractionConstructor: AbstractionConstructor,
        val memberKnotConstructor: KnotConstructor,
        val memberDefinitions: Set<MemberDefinition>,
    ) : ShadowExpression() {
        data class MemberDefinition(
            val name: Identifier,
            val initializer: ShadowExpression,
        ) {
            fun toEntry() = UnorderedTupleConstructor.Entry(
                key = name,
                value = lazyOf(initializer.rawExpression),
            )
        }

        companion object {
            val builtinScopeMemberName = MemberName("com.github.cubuspl42.sigmaLang", "BuiltinScope")
        }

        override val rawExpression: Expression
            get() = rootAbstractionConstructor

        val main: Value
            get() {
                val rootAbstraction = rootAbstractionConstructor.bind(
                    scope = DynamicScope.Bottom,
                ).value as ExpressedAbstraction

                return rootAbstraction.call(
                    argument = UnorderedTuple(
                        valueByKey = mapOf(
                            Identifier(name = "builtin") to lazyOf(BuiltinScope),
                        )
                    ),
                )
            }

        fun generateCode(packageName: String, name: String): FileSpec {
            val context = CodegenRepresentationContext().apply {
                visitOnce(rawExpression)
            }

            val rootRepresentation = context.getRepresentation(rootAbstractionConstructor)

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
                ).addProperty(
                    PropertySpec.builder(
                        name = "main",
                        type = CodegenRepresentationContext.valueTypeName,
                    ).initializer(
                        Call.generateCallCode(
                            CodeBlock.of("root"),
                            UnorderedTupleConstructor.generateCode(
                                valueByKey = mapOf(
                                    Identifier(name = "builtin") to CodeBlock.of(
                                        "%M", builtinScopeMemberName
                                    ).wrapWithLazyOf(),
                                ),
                            ),
                        ),
                    ).build()
                ).build()
            ).build()
        }
    }

    data class Reference(
        val rawKnotReference: Expression,
    ) {
        fun referDefinition(
            referredDefinitionName: Identifier,
        ): Expression = rawKnotReference.readField(
            fieldName = referredDefinitionName,
        )
    }

    abstract class MemberDefinitionBuilder(
        private val name: Identifier,
    ) {
        abstract fun buildInitializer(
            moduleReference: Reference,
        ): ExpressionBuilder<*>

        fun build(
            moduleReference: Reference,
            buildContext: Expression.BuildContext,
        ) = Constructor.MemberDefinition(
            name = name,
            initializer = buildInitializer(
                moduleReference = moduleReference,
            ).build(
                buildContext = buildContext,
            ),
        )
    }

    fun build(): Constructor {
        val (
            rootAbstractionConstructor,
            innerModuleConstructor,
        ) = AbstractionConstructor.looped { argumentReference ->
            val buildContext = Expression.BuildContext(
                builtin = argumentReference.readField(
                    fieldName = ModuleTerm.builtinIdentifier,
                ),
            )

            val (
                memberKnotConstructor,
                mostInnerPartialConstructor,
            ) = KnotConstructor.looped { knotReference ->
                val moduleReference = Reference(
                    rawKnotReference = knotReference,
                )

                val memberDefinitions = memberDefinitionBuilders.mapUniquely {
                    it.build(
                        moduleReference = moduleReference,
                        buildContext = buildContext,
                    )
                }

                val rootTupleConstructor = UnorderedTupleConstructor.fromEntries(
                    memberDefinitions.mapUniquely {
                        it.toEntry()
                    },
                )

                Pair(
                    rootTupleConstructor,
                    object : MostInnerPartialConstructor {
                        override val memberDefinitions = memberDefinitions
                    },
                )
            }

            Pair(
                memberKnotConstructor,
                object : InnerPartialConstructor, MostInnerPartialConstructor by mostInnerPartialConstructor {
                    override val memberKnotConstructor = memberKnotConstructor
                },
            )
        }

        return Constructor(
            rootAbstractionConstructor = rootAbstractionConstructor,
            memberKnotConstructor = innerModuleConstructor.memberKnotConstructor,
            memberDefinitions = innerModuleConstructor.memberDefinitions,
        )
    }
}

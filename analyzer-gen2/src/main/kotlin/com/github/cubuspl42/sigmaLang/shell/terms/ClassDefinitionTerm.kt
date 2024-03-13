package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ClassConstructor
import com.github.cubuspl42.sigmaLang.core.ClassReference
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.ExpressionScope
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class ClassDefinitionTerm(
    override val name: IdentifierTerm,
    val constructor: ConstructorDeclarationTerm?,
    val methodDefinitions: List<MethodDefinitionTerm>,
) : ModuleTerm.DefinitionTerm() {
    data class ConstructorDeclarationTerm(
        val name: IdentifierTerm,
        val argumentType: UnorderedTupleTypeConstructorTerm,
    ) : Wrappable {
        companion object {
            fun build(
                ctx: SigmaParser.ClassConstructorDeclarationContext,
            ): ConstructorDeclarationTerm = ConstructorDeclarationTerm(
                name = IdentifierTerm.build(ctx.name),
                argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
            )
        }

        override fun wrap(): Value {
            TODO("Not yet implemented")
        }
    }

    data class MethodDefinitionTerm(
        val name: IdentifierTerm,
        val implementation: AbstractionConstructorTerm,
    ) : Wrappable {
        companion object {
            fun build(
                ctx: SigmaParser.FunctionDefinitionContext,
            ): MethodDefinitionTerm = MethodDefinitionTerm(
                name = IdentifierTerm.build(ctx.name),
                implementation = AbstractionConstructorTerm(
                    argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
                    image = ExpressionTerm.build(ctx.body),
                ),
            )
        }

        fun buildMethodDefinitionConfig(
            formationContext: FormationContext,
            buildContext: Expression.BuildContext,
        ) = object : ClassConstructor.MethodDefinition.Config() {
            override val name: Identifier
                get() = this@MethodDefinitionTerm.name.toIdentifier()

            override fun createImplementation(
                thisReference: Expression,
            ) = implementation.build(
                formationContext = formationContext.extendScope(
                    innerScope = ExpressionScope(
                        name = Identifier(name = "this"),
                        boundExpression = thisReference,
                    ),
                ),
                buildContext = buildContext,
            ) as AbstractionConstructor
        }

        override fun wrap(): Value = TODO()
    }

    companion object : Term.Builder<SigmaParser.ClassDefinitionContext, ClassDefinitionTerm>() {
        override fun build(
            ctx: SigmaParser.ClassDefinitionContext,
        ): ClassDefinitionTerm = ClassDefinitionTerm(
            name = IdentifierTerm.build(ctx.name),
            constructor = ctx.constructor?.let { ConstructorDeclarationTerm.build(it) },
            methodDefinitions = ctx.methodDefinitions.map { MethodDefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ClassDefinitionContext = parser.classDefinition()
    }

    override fun transmuteInitializer(): ExpressionStub<ClassConstructor> =
        object : ExpressionStub<ClassConstructor>() {
            override fun transform(
                context: FormationContext,
            ): ExpressionBuilder<ClassConstructor> = object : ExpressionBuilder<ClassConstructor>() {
                override fun build(
                    buildContext: Expression.BuildContext,
                ) = ClassConstructor.create(
                    builtinModule = buildContext.builtinModule,
                    config = object : ClassConstructor.Config() {
                        override val tag = name.toIdentifier()

                        override val constructorName = constructor!!.name.toIdentifier()

                        override fun createMethodDefinitions(
                            classReference: ClassReference,
                        ): Set<ClassConstructor.MethodDefinition.Config> =
                            methodDefinitions.mapUniquely { methodDefinitionTerm ->
                                methodDefinitionTerm.buildMethodDefinitionConfig(
                                    formationContext = context,
                                    buildContext = buildContext,
                                )
                            }
                    },
                )
            }
        }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("name") to lazyOf(name.wrap()),
            Identifier.of("constructor") to lazyOf(constructor.wrapOrNil()),
            Identifier.of("methodDefinitions") to lazyOf(methodDefinitions.wrap())
        )
    )
}

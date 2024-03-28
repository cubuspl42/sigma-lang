package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.values.builtin.ClassModule
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

data class ClassDefinitionTerm(
    override val name: Identifier,
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
                implementation = AbstractionConstructorTerm.build(
                    argumentTypeCtx = ctx.argumentType,
                    bodyCtx = ctx.body,
                ),
            )
        }

        fun buildImplementation(
            transmutationContext: TransmutationContext,
        ): AbstractionConstructor = implementation.build(
            transmutationContext = transmutationContext,
            extraArgumentNames = setOf(ClassModule.thisIdentifier),
        )

        override fun wrap(): Value = TODO()
    }

    companion object : Term.Builder<SigmaParser.ClassDefinitionContext, ClassDefinitionTerm>() {
        override fun build(
            ctx: SigmaParser.ClassDefinitionContext,
        ): ClassDefinitionTerm = ClassDefinitionTerm(
            name = IdentifierTerm.build(ctx.name).toIdentifier(),
            constructor = ctx.constructor?.let { ConstructorDeclarationTerm.build(it) },
            methodDefinitions = ctx.methodDefinitions.map { MethodDefinitionTerm.build(it) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.ClassDefinitionContext = parser.classDefinition()
    }

    override fun transmuteInitializer(context: TransmutationContext): Expression {
        val classModule = BuiltinModuleReference.classModule

        return classModule.of.call(
            tag = name,
            instanceConstructorName = constructor!!.name.toIdentifier(),
            methodByName = methodDefinitions.associate { methodDefinitionTerm ->
                methodDefinitionTerm.name.toIdentifier() to methodDefinitionTerm.buildImplementation(
                    transmutationContext = context,
                )
            },
        )
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("name") to lazyOf(name),
            Identifier.of("constructor") to lazyOf(constructor.wrapOrNil()),
            Identifier.of("methodDefinitions") to lazyOf(methodDefinitions.wrap())
        )
    )
}

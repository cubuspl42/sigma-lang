package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.stubs.ClassStub
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
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.ClassConstructorDeclarationContext,
            ): ConstructorDeclarationTerm = ConstructorDeclarationTerm(
                name = IdentifierTerm.build(ctx.name),
                argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
            )
        }
    }

    data class MethodDefinitionTerm(
        val name: IdentifierTerm,
        val abstractionConstructor: AbstractionConstructorTerm,
    ) {
        companion object {
            fun build(
                ctx: SigmaParser.FunctionDefinitionContext,
            ): MethodDefinitionTerm = MethodDefinitionTerm(
                name = IdentifierTerm.build(ctx.name),
                abstractionConstructor = AbstractionConstructorTerm(
                    argumentType = UnorderedTupleTypeConstructorTerm.build(ctx.argumentType),
                    image = ExpressionTerm.build(ctx.body),
                ),
            )
        }

        fun transmute() = ClassStub.MethodDefinitionStub(
            name = name.transmute(),
            methodConstructorStub = abstractionConstructor.transmute(),
        )
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

    override fun transmuteInitializer(): ExpressionStub<*> = ClassStub.of(
        tag = name.transmute(),
        constructorName = constructor!!.name.transmute(),
        methodDefinitionStubs = methodDefinitions.mapUniquely {
            it.transmute()
        },
    )
}

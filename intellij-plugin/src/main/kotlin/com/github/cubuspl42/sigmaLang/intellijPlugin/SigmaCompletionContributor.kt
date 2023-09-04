package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaReferenceExpression
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Module
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModuleResolver
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaFile
import com.intellij.codeInsight.completion.*

class SigmaCompletionContributor : CompletionContributor() {
    init {
        val prelude = Project.loadPrelude()

        val completionProvider = object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: com.intellij.util.ProcessingContext,
                resultSet: CompletionResultSet,
            ) {
                val referenceElement = parameters.position.parent as SigmaReferenceExpression
                val referenceTerm = referenceElement.asTerm

                val file = referenceElement.containingFile as SigmaFile
                val moduleTerm = file.asTerm

                val module = Module.build(
                    outerScope = prelude.innerStaticScope,
                    moduleResolver = ModuleResolver.Empty,
                    term = moduleTerm,
                )

                val reference = module.expressionMap.getMappedExpression(referenceTerm)

                reference?.outerScope?.getAllNames()?.forEach {
                    resultSet.addElement(
                        LookupElementBuilder.create(it.name),
                    )
                }
            }
        }

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(SigmaLanguage).withParent(SigmaReferenceExpression::class.java),
            completionProvider,
        )
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
    }
}

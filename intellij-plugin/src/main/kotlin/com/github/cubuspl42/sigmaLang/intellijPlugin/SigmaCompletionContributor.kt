package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Module
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModuleResolver
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaFile
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaReferenceExpression
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns

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
                    modulePath = ModulePath(name = "__module__"),
                    term = moduleTerm,
                )

                val reference = module.expressionMap.getMappedExpression(referenceTerm)

                reference?.outerScope?.getAllNames()?.forEach {
                    resultSet.addElement(
                        LookupElementBuilder.create(it.dump()),
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

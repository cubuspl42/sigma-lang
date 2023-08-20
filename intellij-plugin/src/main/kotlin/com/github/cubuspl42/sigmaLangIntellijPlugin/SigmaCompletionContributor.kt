package com.github.cubuspl42.sigmaLangIntellijPlugin

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import sigma.semantics.BuiltinScope

class SigmaCompletionContributor : CompletionContributor() {
    init {
        val completionProvider = object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: com.intellij.util.ProcessingContext,
                resultSet: CompletionResultSet,
            ) {
                BuiltinScope.names.forEach {
                    resultSet.addElement(
                        LookupElementBuilder.create(it.name),
                    )
                }
            }
        }

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withLanguage(SigmaLanguage)
                .withParent(SigmaExpression::class.java),
            completionProvider,
        )
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
    }
}

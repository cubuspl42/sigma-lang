package com.github.cubuspl42.sigmaLangIntellijPlugin

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaReferenceExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl.SigmaLetExpressionImpl
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope

class SigmaCompletionContributor : CompletionContributor() {
    init {
        val completionProvider = object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: com.intellij.util.ProcessingContext,
                resultSet: CompletionResultSet,
            ) {
                val element = parameters.position.parent as SigmaReferenceExpression
                val term = element.toTerm()

                BuiltinScope.names.forEach {
                    resultSet.addElement(
                        LookupElementBuilder.create(it.name),
                    )
                }

                val grandparent = element.parent?.parent

                if (grandparent is SigmaLetExpressionImpl) {
                    val grandparentTerm = grandparent.toTerm()

                    grandparent.getNames().forEach {
                        resultSet.addElement(
                            LookupElementBuilder.create(it),
                        )
                    }
                }
            }
        }

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withLanguage(SigmaLanguage)
                .withParent(SigmaReferenceExpression::class.java),
            completionProvider,
        )
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
    }
}

package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaReferenceExpression
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl.SigmaLetExpressionImpl
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Module
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Prelude
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaFile
import com.intellij.codeInsight.completion.*

class SigmaCompletionContributor : CompletionContributor() {
    init {
        val prelude = Prelude.load()

        val completionProvider = object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: com.intellij.util.ProcessingContext,
                resultSet: CompletionResultSet,
            ) {
                val element = parameters.position.parent as SigmaReferenceExpression
                val elementTerm = element.asTerm

                val file = parameters.originalFile as SigmaFile
                val moduleTerm = file.asTerm


//                val module = Module.build(
//                    prelude = prelude,
//                    term = moduleTerm,
//                )


                BuiltinScope.names.forEach {
                    resultSet.addElement(
                        LookupElementBuilder.create(it.name),
                    )
                }

                val grandparent = element.parent?.parent

                if (grandparent is SigmaLetExpressionImpl) {
                    val grandparentTerm = grandparent.asTerm

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
            PlatformPatterns.psiElement().withLanguage(SigmaLanguage).withParent(SigmaReferenceExpression::class.java),
            completionProvider,
        )
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
    }
}

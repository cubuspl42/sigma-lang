// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.github.cubuspl42.sigmaLang.intellijPlugin.parser.SigmaParser
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaFile
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class SigmaParserDefinition : ParserDefinition {
    companion object {
        val File = IFileElementType(SigmaLanguage)

        val Comments = TokenSet.create(SigmaTypes.LINE_COMMENT)
    }

    override fun createLexer(project: Project): Lexer = SigmaLexerAdapter()

    override fun getCommentTokens(): TokenSet = Comments

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project): PsiParser = SigmaParser()

    override fun getFileNodeType(): IFileElementType = File

    override fun createFile(viewProvider: FileViewProvider): PsiFile = SigmaFile(viewProvider)

    override fun createElement(node: ASTNode): PsiElement = SigmaTypes.Factory.createElement(node)
}

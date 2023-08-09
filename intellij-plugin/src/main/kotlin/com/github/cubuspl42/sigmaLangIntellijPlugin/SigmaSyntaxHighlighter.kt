// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.cubuspl42.sigmaLangIntellijPlugin

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class SigmaSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        private val Keyword: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        private val IntLiteral: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_INT_LITERAL", DefaultLanguageHighlighterColors.NUMBER)

        private val Comment: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        private val BadCharacter: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BadCharKeys = arrayOf(BadCharacter)

        private val KeywordKeys = arrayOf(Keyword)

        private val IntLiteralKeys = arrayOf(IntLiteral)

        private val CommentKeys = arrayOf(Comment)

        private val EmptyKeys = arrayOfNulls<TextAttributesKey>(0)
    }

    override fun getHighlightingLexer(): Lexer = SigmaLexerAdapter()

    override fun getTokenHighlights(
        tokenType: IElementType,
    ): Array<out TextAttributesKey?> = when (tokenType) {
        SigmaTypes.CONST_KEYWORD, SigmaTypes.NAMESPACE_KEYWORD -> KeywordKeys
        SigmaTypes.INT_LITERAL -> IntLiteralKeys
        TokenType.BAD_CHARACTER -> BadCharKeys
        else -> EmptyKeys
    }
}

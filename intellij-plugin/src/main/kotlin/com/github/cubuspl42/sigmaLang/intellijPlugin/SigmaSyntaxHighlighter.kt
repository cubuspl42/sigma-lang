// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTypes
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

        private val Number: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_NUMBER", DefaultLanguageHighlighterColors.NUMBER)

        private val Comment: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        private val Comma: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_COMMA", DefaultLanguageHighlighterColors.COMMA)

        private val Bracket: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)

        private val Identifier: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)

        private val BadCharacter: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SIGMA_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BadCharKeys = arrayOf(BadCharacter)

        private val KeywordKeys = arrayOf(Keyword)

        private val NumberKeys = arrayOf(Number)

        private val CommaKeys = arrayOf(Comma)

        private val BracketKeys = arrayOf(Bracket)

        private val CommentKeys = arrayOf(Comment)

        private val IdentifierKeys = arrayOf(Identifier)

        private val EmptyKeys = arrayOfNulls<TextAttributesKey>(0)
    }

    override fun getHighlightingLexer(): Lexer = SigmaLexerAdapter()

    override fun getTokenHighlights(
        tokenType: IElementType,
    ): Array<out TextAttributesKey?> = when (tokenType) {
        SigmaTypes.LINE_COMMENT -> CommentKeys
        SigmaTypes.IF_KEYWORD, SigmaTypes.THEN_KEYWORD, SigmaTypes.ELSE_KEYWORD, SigmaTypes.IS_UNDEFINED_KEYWORD, SigmaTypes.LET_KEYWORD, SigmaTypes.IN_KEYWORD, SigmaTypes.NAMESPACE_KEYWORD, SigmaTypes.CONST_KEYWORD -> KeywordKeys
        SigmaTypes.INT -> NumberKeys
        SigmaTypes.COMMA -> CommaKeys
        SigmaTypes.BRACKET_LEFT, SigmaTypes.BRACKET_RIGHT -> BracketKeys
        SigmaTypes.IDENTIFIER -> IdentifierKeys
        TokenType.BAD_CHARACTER -> BadCharKeys
        else -> EmptyKeys
    }
}

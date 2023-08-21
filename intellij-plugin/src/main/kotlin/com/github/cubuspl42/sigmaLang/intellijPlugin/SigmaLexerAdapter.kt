package com.github.cubuspl42.sigmaLang.intellijPlugin

import com.intellij.lexer.FlexAdapter

class SigmaLexerAdapter : FlexAdapter(SigmaLexer(null))

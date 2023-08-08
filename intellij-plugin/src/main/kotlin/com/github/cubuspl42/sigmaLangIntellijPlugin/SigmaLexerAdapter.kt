package com.github.cubuspl42.sigmaLangIntellijPlugin

import com.intellij.lexer.FlexAdapter

class SigmaLexerAdapter : FlexAdapter(SigmaLexer(null))

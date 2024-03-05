package com.github.cubuspl42.sigmaLang.shell

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException

object ThrowingErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: org.antlr.v4.runtime.RecognitionException?,
    ) {
        throw ParseCancellationException("line $line:$charPositionInLine $msg")
    }
}

package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.intellijPlugin.SigmaLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class SigmaTokenType(
    @NonNls debugName: String,
) : IElementType(debugName, SigmaLanguage) {
    override fun toString(): String = "SigmaTokenType." + super.toString()
}

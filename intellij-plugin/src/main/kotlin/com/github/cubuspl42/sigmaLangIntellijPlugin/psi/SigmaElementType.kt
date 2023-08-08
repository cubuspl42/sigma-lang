package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.github.cubuspl42.sigmaLangIntellijPlugin.SigmaLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class SigmaElementType(
    @NonNls debugName: String,
) : IElementType(debugName, SigmaLanguage)

package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.github.cubuspl42.sigmaLangIntellijPlugin.SigmaFileType
import com.github.cubuspl42.sigmaLangIntellijPlugin.SigmaLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class SigmaFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, SigmaLanguage) {
    override fun getFileType(): FileType = SigmaFileType

    override fun toString(): String = "Sigma File"
}

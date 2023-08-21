package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.intellijPlugin.SigmaFileType
import com.github.cubuspl42.sigmaLang.intellijPlugin.SigmaLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class SigmaFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, SigmaLanguage) {
    override fun getFileType(): FileType = SigmaFileType

    override fun toString(): String = "Sigma File"
}

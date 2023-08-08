package com.github.cubuspl42.sigmaLangIntellijPlugin

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object SigmaFileType : LanguageFileType(SigmaLanguage) {
    override fun getName(): String = "Sigma File"

    override fun getDescription(): String = "Sigma language file"

    override fun getDefaultExtension(): String = "sigma"

    override fun getIcon(): Icon = SigmaIcons.File
}

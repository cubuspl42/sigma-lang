package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.analyzer.syntax.ImportTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.SigmaFileType
import com.github.cubuspl42.sigmaLang.intellijPlugin.SigmaLanguage
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.descendantsOfType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.childrenOfType

class SigmaFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, SigmaLanguage) {
    override fun getFileType(): FileType = SigmaFileType

    override fun toString(): String = "Sigma File"

    val asTerm = object  : ModuleTerm {
        override val imports: List<ImportTerm>
            get() = emptyList()

        override val namespaceEntries: List<NamespaceEntryTerm>
            get() = this@SigmaFile.namespaceEntries.map { it.asTerm }
    }

    val namespaceEntries: Collection<SigmaNamespaceEntry>
        get() = this.descendantsOfType<SigmaNamespaceEntry>()
}

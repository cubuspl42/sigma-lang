package com.github.cubuspl42.sigmaLangIntellijPlugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.cubuspl42.sigmaLangIntellijPlugin.MyBundle
import sigma.evaluation.values.IntValue
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.IntLiteralTerm

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")

        IntLiteralTerm(
            location = SourceLocation(0, 0),
            value = IntValue.Zero,
        )

    }

    fun getRandomNumber() = (1..100).random()
}

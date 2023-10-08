package com.github.cubuspl42.sigmaLang.applications.html

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Project
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResourceProjectStore

fun loadHtmlProject(): Project {
    val projectStore = ResourceProjectStore(javaClass = object {}.javaClass)

    return Project.Loader.create().load(
        projectStore = projectStore,
    )
}

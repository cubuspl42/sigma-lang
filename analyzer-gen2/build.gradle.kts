plugins {
    alias(libs.plugins.kotlin)
    antlr
    application
}

group = "me.kuba"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

dependencies {
    antlr("org.antlr:antlr4:4.13.1")

    implementation(kotlin("stdlib"))
    implementation(libs.kotlinxSerialization)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks {
    generateGrammarSource {
        maxHeapSize = "64m"
        arguments = arguments + listOf("-package", "com.github.cubuspl42.sigmaLang.analyzer.parser.antlr", "-visitor", "-no-listener")
        outputDirectory = File("${project.buildDir}/generated-src/antlr/main/com/github/cubuspl42/sigmaLang/analyzer/parser/antlr")
    }

    compileKotlin {
        dependsOn(generateGrammarSource)
    }

    compileTestKotlin {
        dependsOn(generateTestGrammarSource)
    }
}

application {
    mainClass.set("MainKt")
}

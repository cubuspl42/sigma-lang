import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

configurations {
    create("testIntegrationImplementation") {
        extendsFrom(configurations["testImplementation"])
    }
}

dependencies {
    antlr("org.antlr:antlr4:4.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

sourceSets {
    create("testIntegration") {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

tasks {
    register<Test>("testIntegration") {
        testClassesDirs = sourceSets["testIntegration"].output.classesDirs
        classpath = sourceSets["testIntegration"].runtimeClasspath
    }

    test {
        dependsOn("testIntegration")
    }

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

    getByName("compileTestIntegrationKotlin") {
        dependsOn("generateTestIntegrationGrammarSource")
    }
}

application {
    mainClass.set("MainKt")
}

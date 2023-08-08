import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.ben-manes.versions")
    antlr
    application
}

group = "me.kuba"
version = "1.0-SNAPSHOT"

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

    generateGrammarSource {
        maxHeapSize = "64m"
        arguments = arguments + listOf("-package", "sigma.parser.antlr", "-visitor", "-no-listener")
        outputDirectory = File("${project.buildDir}/generated-src/antlr/main/sigma/parser/antlr")
    }

    test {
        dependsOn("testIntegration")
    }

    withType<Test> {
        dependsOn(generateTestGrammarSource)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"

        dependsOn(generateGrammarSource)
    }
}

application {
    mainClass.set("MainKt")
}

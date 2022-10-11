import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    antlr
    application
}

group = "me.kuba"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.11.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-package", "sigma.parser.antlr", "-visitor", "-no-listener")
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/sigma/parser/antlr")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"

    dependsOn(tasks.generateGrammarSource)
}

application {
    mainClass.set("MainKt")
}

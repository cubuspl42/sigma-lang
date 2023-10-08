plugins {
    alias(libs.plugins.kotlin)
    application
}

group = "me.kuba"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":analyzer"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

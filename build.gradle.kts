plugins {
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleVersions)
}

allprojects {
    repositories {
        mavenCentral()
    }
}

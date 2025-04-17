dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.nayrid.com/public/")
            name = "nayrid"
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "event"

sequenceOf(
    "api",
).forEach {
    include("${rootProject.name}-$it")
    project(":${rootProject.name}-$it").projectDir = file("${rootProject.name}-$it")
}

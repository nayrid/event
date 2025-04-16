import com.diffplug.gradle.spotless.FormatExtension

plugins {
    `java-library`
    idea
    id("net.kyori.indra")
    id("net.kyori.indra.crossdoc")
    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.licenser.spotless")
    `maven-publish`
}

indra {
    mitLicense()

    javaVersions {
        target(21)
    }
}

val isSnapshot = (rootProject.version as String).contains(("SNAPSHOT"))

indraCrossdoc {
    baseUrl().set(providers.gradleProperty("javadocPublishRoot").get().replace("%REPO%", if(isSnapshot) "snapshots" else "releases"))
    nameBasedDocumentationUrlProvider {
        version = (project.version as String) + "/raw"
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("license.txt"))
}

spotless {
    fun FormatExtension.applyCommon() {
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)
    }
    java {
        importOrderFile(rootProject.file(".spotless/nayrid.importorder"))
        applyCommon()
    }
    kotlinGradle {
        applyCommon()
    }
}

dependencies {
    checkstyle(libs.stylecheck)

    compileOnlyApi(libs.jspecify)
    compileOnlyApi(libs.jetbrains.annotations)

    testImplementation(libs.junit.jupiterApi)
    testRuntimeOnly(libs.junit.jupiterEngine)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).apply {
            isFailOnError = false
            links(
                "https://jspecify.dev/docs/api/",
                "https://javadoc.io/doc/org.jetbrains/annotations/26.0.2/",
                "https://javadoc.io/doc/net.kyori/examination-api/1.3.0",
                "https://jd.advntr.dev/key/${libs.versions.net.kyori.adventure.get()}/", 
                "https://repo.nayrid.com/javadoc/${if (libs.versions.com.nayrid.common.get().endsWith("-SNAPSHOT")) "snapshots" else "releases"}/com/nayrid/common/common-api/${libs.versions.com.nayrid.common.get()}/raw/",
                "https://repo.nayrid.com/javadoc/${if (libs.versions.com.nayrid.common.get().endsWith("-SNAPSHOT")) "snapshots" else "releases"}/com/nayrid/common/common-examine/${libs.versions.com.nayrid.common.get()}/raw/",
            )
            tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:",
                "param",
                "return",
                "throws",
                "since",
                "version",
                "serialData",
                "see"
            )
        }
    }
    test {
        useJUnitPlatform()
    }
}

publishing {
    repositories {
        maven {
            name = "nayridSnapshots"
            url = uri("https://repo.nayrid.com/snapshots")
            credentials {
                username = project.findProperty("nayridUsername") as String?
                    ?: System.getenv("MAVEN_NAME")
                password = project.findProperty("nayridPassword") as String?
                    ?: System.getenv("MAVEN_TOKEN")
            }
        }
        maven {
            name = "nayridReleases"
            url = uri("https://repo.nayrid.com/releases")
            credentials {
                username = project.findProperty("nayridUsername") as String?
                    ?: System.getenv("MAVEN_NAME")
                password = project.findProperty("nayridPassword") as String?
                    ?: System.getenv("MAVEN_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group as String + "." + rootProject.name
            version = rootProject.version as String
            from(components["java"])
        }
    }
}

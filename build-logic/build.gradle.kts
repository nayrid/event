plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven(url = "https://repo.stellardrift.ca/repository/internal/") {
        name = "stellardriftReleases"
        mavenContent { releasesOnly() }
    }
    maven(url = "https://repo.stellardrift.ca/repository/snapshots/") {
        name = "stellardriftSnapshots"
        mavenContent { snapshotsOnly() }
    }
}

dependencies {
    implementation(libs.indra.common)
    implementation(libs.indra.licenseHeader)
    implementation(libs.indra.crossdoc)

    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

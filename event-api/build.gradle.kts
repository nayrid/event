plugins {
    id("event.base-conventions")
    id("me.champeau.jmh") version "0.7.3"
}

dependencies {
    api(libs.common.api)
    api(libs.common.examine)
    jmh(libs.common.api)
    jmh(libs.common.examine)

    api(platform(libs.adventure.bom))
    jmh(platform(libs.adventure.bom))
    api("net.kyori:adventure-key")
    jmh("net.kyori:adventure-key")
}

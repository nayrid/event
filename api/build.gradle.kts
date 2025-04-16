plugins {
    id("event.base-conventions")
    id("me.champeau.jmh") version "0.7.3"
}

dependencies {
    api(libs.common)
    jmh(libs.common)

    api(libs.caffeine)
    jmh(libs.caffeine)

    api(platform(libs.adventure.bom))
    jmh(platform(libs.adventure.bom))
    api("net.kyori:adventure-key")
    jmh("net.kyori:adventure-key")
}
